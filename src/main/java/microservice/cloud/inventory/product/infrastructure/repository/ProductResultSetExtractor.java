package microservice.cloud.inventory.product.infrastructure.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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

        while (rs.next()) {
            String productId = rs.getString("prod_id");

            ProductReadDTO product = productsMap.get(productId);
            if (product == null) {
                product = new ProductReadDTO();
                product.setId(productId);
                product.setTitle(rs.getString("title"));
                product.setDescription(rs.getString("description"));
                product.setSlug(rs.getString("slug"));
                product.setPrice(rs.getDouble("price"));
                product.setStock(rs.getInt("stock"));
                product.setAttributes(new ArrayList<>());

                java.sql.Array imagesArr = rs.getArray("images");
                product.setImages(imagesArr != null ? List.of((String[]) imagesArr.getArray()) : new ArrayList<>());

                java.sql.Array tagsArr = rs.getArray("tags");
                product.setTags(tagsArr != null ? List.of((String[]) tagsArr.getArray()) : new ArrayList<>());

                productsMap.put(productId, product);
            }

            if(rs.getString("prod_category_id") != null) {
                product
                    .getCategories()
                    .add(
                        rs.getString("prod_category_id")
                    );
            }

            String valId = rs.getString("val_id");
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
    }}
