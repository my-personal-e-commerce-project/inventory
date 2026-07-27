package microservice.cloud.inventory.category.infrastructure.persistence.repository;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.ResultSetExtractor;

import microservice.cloud.inventory.attribute.infrastructure.persistence.model.AttributeDefinitionEntity;
import microservice.cloud.inventory.category.infrastructure.persistence.model.CategoryAttributeEntity;

public class CategoryAttributeResultSetExtractor implements ResultSetExtractor<CategoryAttributeEntity>{

    @Override
    public CategoryAttributeEntity extractData(ResultSet rs) throws SQLException {
        CategoryAttributeEntity entity = null;

        while (rs.next()) {
            if (entity == null) {
                if (rs.getString("attr_id") != null) {
                    AttributeDefinitionEntity def = new AttributeDefinitionEntity(
                        rs.getString("def_id"),
                        rs.getString("def_name"),
                        rs.getString("def_slug"),
                        rs.getString("def_type"),
                        rs.getBoolean("def_is_global"),
                        rs.getLong("version")
                    );

                    entity = new CategoryAttributeEntity(
                        rs.getString("attr_id"),
                        rs.getString("attr_cat_id"),
                        rs.getString("attr_def_id"),
                        def,
                        rs.getBoolean("attr_is_required"),
                        rs.getBoolean("attr_is_sortable"),
                        rs.getBoolean("attr_is_filterable")
                    );
                }
            }
        }

        return entity;
    }
}
