package microservice.cloud.inventory.category.infrastructure.repository;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import microservice.cloud.inventory.attribute.infrastructure.entity.AttributeDefinitionEntity;
import microservice.cloud.inventory.category.domain.entity.CategoryAttribute;
import microservice.cloud.inventory.category.domain.repository.CategoryAttributeRepository;
import microservice.cloud.inventory.category.infrastructure.entity.CategoryAttributeEntity;
import microservice.cloud.inventory.shared.domain.value_objects.Id;

@Repository
@RequiredArgsConstructor
public class CategoryAttributeRepositoryImpl implements CategoryAttributeRepository {

    private final EntityManager entityManager;

    @Override
    public void save(CategoryAttribute categoryAttribute) {
        AttributeDefinitionEntity attributeDefinition = AttributeDefinitionEntity.builder()
            .id(categoryAttribute.attribute_definition().id().value())
            .name(categoryAttribute.attribute_definition().name())
            .slug(categoryAttribute.attribute_definition().slug())
            .type(categoryAttribute.attribute_definition().type().toString())
            .is_global(categoryAttribute.attribute_definition().isGlobal())
            .build();

        entityManager.persist(attributeDefinition);
        
        attributeDefinition = entityManager.find(
            AttributeDefinitionEntity.class, 
            categoryAttribute.attribute_definition().id().value()
        );

        entityManager.persist(CategoryAttributeEntity.builder()
            .id(categoryAttribute.id().value())
            .attribute_definition(attributeDefinition)
            .is_required(categoryAttribute.is_required())
            .is_filterable(categoryAttribute.is_filterable())
            .is_sortable(categoryAttribute.is_sortable())
            .build()
        );
    }

    @Override
    public void update(CategoryAttribute categoryAttribute) {
        AttributeDefinitionEntity attributeDefinition = AttributeDefinitionEntity.builder()
            .id(categoryAttribute.attribute_definition().id().value())
            .name(categoryAttribute.attribute_definition().name())
            .slug(categoryAttribute.attribute_definition().slug())
            .type(categoryAttribute.attribute_definition().type().toString())
            .is_global(categoryAttribute.attribute_definition().isGlobal())
            .build();

        entityManager.persist(attributeDefinition);
        
        attributeDefinition = entityManager.find(
            AttributeDefinitionEntity.class, 
            categoryAttribute.attribute_definition().id().value()
        );

        entityManager.persist(CategoryAttributeEntity.builder()
            .id(categoryAttribute.id().value())
            .attribute_definition(attributeDefinition)
            .is_required(categoryAttribute.is_required())
            .is_filterable(categoryAttribute.is_filterable())
            .is_sortable(categoryAttribute.is_sortable())
            .build()
        );
    }

    @Override
    public void delete(Id id) {
        entityManager.remove(entityManager.find(CategoryAttributeEntity.class, id.value()));        
    }
}
