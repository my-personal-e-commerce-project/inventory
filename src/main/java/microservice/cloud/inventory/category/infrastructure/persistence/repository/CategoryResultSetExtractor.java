package microservice.cloud.inventory.category.infrastructure.persistence.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;

import org.springframework.jdbc.core.ResultSetExtractor;

import microservice.cloud.inventory.attribute.infrastructure.persistence.model.AttributeDefinitionEntity;
import microservice.cloud.inventory.category.infrastructure.persistence.model.*;

public class CategoryResultSetExtractor implements ResultSetExtractor<CategoryEntity> {

    @Override
    public CategoryEntity extractData(ResultSet rs) throws SQLException {
        CategoryEntity entity = null;

        while (rs.next()) {
            
            if (entity == null) {
                entity = new CategoryEntity(
                    rs.getString("cat_id"),
                    rs.getString("cat_name"),
                    rs.getString("cat_slug"),
                    rs.getString("cat_parent_id"),
                    new HashSet<>()
                );
            }

            if (rs.getString("attr_id") != null) {
                AttributeDefinitionEntity def = new AttributeDefinitionEntity(
                    rs.getString("def_id"),
                    rs.getString("def_name"),
                    rs.getString("def_slug"),
                    rs.getString("def_type"),
                    rs.getBoolean("def_is_global")
                );

                CategoryAttributeEntity attr = new CategoryAttributeEntity(
                    rs.getString("attr_id"),
                    rs.getString("attr_cat_id"),
                    rs.getString("attr_def_id"),
                    def,
                    rs.getBoolean("attr_is_required"),
                    rs.getBoolean("attr_is_sortable"),
                    rs.getBoolean("attr_is_filterable")
                );
                
                entity.getCategoryAttributes().add(attr);
            }
        }

        return entity;
    }
}

