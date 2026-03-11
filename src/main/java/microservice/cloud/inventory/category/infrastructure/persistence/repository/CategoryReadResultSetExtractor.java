package microservice.cloud.inventory.category.infrastructure.persistence.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import microservice.cloud.inventory.category.application.dtos.AttributeDefinitionReadDTO;
import microservice.cloud.inventory.category.application.dtos.CategoryAttributeReadDTO;
import microservice.cloud.inventory.category.application.dtos.CategoryReadDTO;

public class CategoryReadResultSetExtractor implements ResultSetExtractor<List<CategoryReadDTO>> {

    @Override
    public List<CategoryReadDTO> extractData(ResultSet rs) 
        throws SQLException, DataAccessException 
    {
        Map<String, CategoryReadDTO> categoriesMap = new LinkedHashMap<>();

        while (rs.next()) {
            String categoryId = rs.getString("cat_id");

            CategoryReadDTO category = categoriesMap.get(categoryId);
            if (category == null) {
                category = new CategoryReadDTO();
                category.setId(categoryId);
                category.setName(rs.getString("cat_name"));
                category.setSlug(rs.getString("cat_slug"));
                category.setParent_slug(rs.getString("cat_parent_slug"));
                category.setCategoryAttributes(new ArrayList<>());

                categoriesMap.put(categoryId, category);
            }

            String attrDef = rs.getString("def_id");
            if(attrDef != null) {

                category.getCategoryAttributes().add(
                    new CategoryAttributeReadDTO(
                        rs.getString("attr_id"),
                        new AttributeDefinitionReadDTO(
                            rs.getString("def_id"),
                            rs.getString("def_name"),
                            rs.getString("def_slug"),
                            rs.getString("def_type"),
                            rs.getBoolean("def_is_global")
                        ),
                        Boolean.valueOf(rs.getBoolean("attr_is_required")),
                        Boolean.valueOf(rs.getBoolean("attr_is_filterable")),
                        Boolean.valueOf(rs.getBoolean("attr_is_sortable"))
                    )
                );
            }

        }

        return new ArrayList<>(categoriesMap.values());

    }
}
