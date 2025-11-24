package microservice.cloud.inventory.attribute.infrastructure.entity.repository;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import microservice.cloud.inventory.attribute.domain.entity.AttributeDefinition;
import microservice.cloud.inventory.attribute.domain.repository.AttributeDefinitionRepository;
import microservice.cloud.inventory.attribute.domain.value_objects.DataType;
import microservice.cloud.inventory.attribute.infrastructure.entity.AttributeDefinitionEntity;
import microservice.cloud.inventory.category.infrastructure.entity.CategoryAttributeEntity;
import microservice.cloud.inventory.shared.domain.value_objects.Id;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;

@Repository
@RequiredArgsConstructor
public class AttributeDefinitionRepositoryJpaImpl implements AttributeDefinitionRepository {

    private final EntityManager entityManager;

    @Override
    public AttributeDefinition getByCategoryAttributeId(Id categoryAttributeId) {
        CategoryAttributeEntity categoryAttributeEntity = entityManager.find(
            CategoryAttributeEntity.class, 
            categoryAttributeId.value()
        );

        AttributeDefinitionEntity attributeDefinitionEntity = categoryAttributeEntity.getAttribute_definition();

        return toMap(attributeDefinitionEntity);
    }

    @Transactional
    public void deleteByCategoryAttributeId(Id categoryAttributeId){
        CategoryAttributeEntity categoryAttributeEntity = entityManager.find(
            CategoryAttributeEntity.class, 
            categoryAttributeId.value()
        );

        if(categoryAttributeEntity == null)
            return;

        AttributeDefinitionEntity attributeDefinitionEntity = categoryAttributeEntity
            .getAttribute_definition();

        entityManager.remove(attributeDefinitionEntity);
    }


    @Override
    public AttributeDefinition getByProductAttributeId(Id categoryAttributeId) {
        // TODO Auto-generated method stub
        return null;
    }

    private AttributeDefinition toMap(AttributeDefinitionEntity attributeDefinitionEntity) {

        return new AttributeDefinition(
            new Id(attributeDefinitionEntity.getId()), 
            attributeDefinitionEntity.getName(),
            new Slug(attributeDefinitionEntity.getSlug()),
            DataType.valueOf(attributeDefinitionEntity.getType()),
           attributeDefinitionEntity.is_global()
        );

    }
}
