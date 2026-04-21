package microservice.cloud.inventory.product.infrastructure.persistence.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import microservice.cloud.inventory.product.application.dtos.ProductAttributeValueReadDTO;
import microservice.cloud.inventory.product.application.dtos.ProductReadDTO;

public class ProductResultSetExtractor implements ResultSetExtractor<List<ProductReadDTO>> {

    @Override
    public List<ProductReadDTO> extractData(ResultSet rs) 
        throws SQLException, DataAccessException 
    {
        Map<String, ProductReadDTO> productsMap = new LinkedHashMap<>();

        String discount = null;
        String rawCategories = null;
        String valId = null;
        String productId = null;

        while (rs.next()) {
            productId = rs.getString("prod_id");

            ProductReadDTO product = productsMap.get(productId);
            if (product == null) {
                product = new ProductReadDTO(
                    productId,
                    rs.getString("title"),
                    rs.getString("slug"),
                    rs.getString("description"),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new HashSet<>(),
                    rs.getDouble("price"),
                    rs.getInt("stock"),
                    null,
                    null
                );

                java.sql.Array imagesArr = rs.getArray("images");
                product.setImages(imagesArr != null ? List.of((String[]) imagesArr.getArray()) : new ArrayList<>());

                java.sql.Array tagsArr = rs.getArray("tags");
                product.setTags(tagsArr != null ? List.of((String[]) tagsArr.getArray()) : new ArrayList<>());

                productsMap.put(productId, product);
            }   

            discount = rs.getString("discount_id");
            if (discount != null) {
                product.getDiscounts().add(discount);
            }
            
            rawCategories = rs.getString("all_categories");
            if (rawCategories != null && !rawCategories.isBlank()) {
                String[] catsArray = rawCategories.split(",");
                product.setCategories(new ArrayList<>(Arrays.asList(catsArray)));
            }

            valId = rs.getString("val_id");
            if (valId != null) {
                product.getAttributes().add(
                        new ProductAttributeValueReadDTO(
                            valId,
                            rs.getString("attr_slug"),
                            rs.getString("attr_id"),
                            rs.getString("string_value"),
                            rs.getObject("integer_value", Integer.class),
                            rs.getObject("double_value", Double.class),
                            rs.getObject("boolean_value", Boolean.class)
                        )
                    );
            }
        }
        return new ArrayList<>(productsMap.values());
    }
}
