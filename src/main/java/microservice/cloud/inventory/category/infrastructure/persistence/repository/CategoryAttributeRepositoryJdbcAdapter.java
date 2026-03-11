package microservice.cloud.inventory.category.infrastructure.persistence.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import microservice.cloud.inventory.attribute.domain.entity.AttributeDefinition;
import microservice.cloud.inventory.attribute.infrastructure.persistence.model.AttributeDefinitionEntity;
import microservice.cloud.inventory.category.domain.entity.CategoryAttribute;
import microservice.cloud.inventory.category.domain.repository.CategoryAttributeRepository;
import microservice.cloud.inventory.category.infrastructure.persistence.model.CategoryAttributeEntity;
import microservice.cloud.inventory.shared.domain.value_objects.Id;

import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.data.relational.core.conversion.DbActionExecutionException;

@Repository
@RequiredArgsConstructor
public class CategoryAttributeRepositoryJdbcAdapter implements CategoryAttributeRepository {

    private final JdbcAggregateTemplate JdbcAggregateTemplate;

    @Transactional
    @Override
    public void save(Id id, CategoryAttribute categoryAttribute) {
        try {
            JdbcAggregateTemplate.insert(toMap(categoryAttribute.attribute_definition()));
        } catch(DbActionExecutionException e) {
            if (e.getMessage() != null && e.getMessage().contains("slug")) {
                throw new RuntimeException("The slug already exists");
            }
            throw e;
        }
        JdbcAggregateTemplate.insert(toMap(id, categoryAttribute));
    }

    @Transactional
    @Override
    public void delete(Id id) {
        JdbcAggregateTemplate.deleteById(id.value(), CategoryAttributeEntity.class);
    }

    private CategoryAttributeEntity toMap(Id cat, CategoryAttribute entity) {
        return new CategoryAttributeEntity(
            entity.id().value(),
            cat.value(),
            entity.attribute_definition_id().value(),
            new AttributeDefinitionEntity(
                entity.attribute_definition_id().value(),
                entity.attribute_definition().name(),
                entity.attribute_definition().slug().value(),
                entity.attribute_definition().type().toString(),
                entity.attribute_definition().is_global()
            ),
            entity.is_required(), 
            entity.is_filterable(), 
            entity.is_sortable()
        );
    }

    private AttributeDefinitionEntity toMap(AttributeDefinition attr) {
   
        return new AttributeDefinitionEntity(
            attr.id().value(), 
            attr.name(), 
            attr.slug().value(), 
            attr.type().toString(), 
            attr.is_global()
        );
    }
}
