package microservice.cloud.inventory.attribute.infrastructure.entity.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
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
    public Map<String, AttributeDefinition> getListByIds(List<String> ids) {
        List<String> attributeDefinitionIds = new ArrayList<>(ids);

        List<AttributeDefinitionEntity> definitions = entityManager.createQuery(
                "SELECT a FROM AttributeDefinitionEntity a WHERE a.id IN :ids", AttributeDefinitionEntity.class
            )
            .setParameter("ids", attributeDefinitionIds)
            .getResultList();

        if (definitions.size() != attributeDefinitionIds.size()) {
            Set<String> foundIds = definitions.stream()
                .map(AttributeDefinitionEntity::getId)
                .collect(Collectors.toSet());
            attributeDefinitionIds.removeAll(foundIds);
            throw new EntityNotFoundException("Attribute definitions not found: " + attributeDefinitionIds);
        }

        Map<String, AttributeDefinition> map = new HashMap<>();

        definitions.stream().forEach(attr -> {

             map.put(attr.getId(), new AttributeDefinition(
                new Id(attr.getId()), 
                attr.getName(), 
                new Slug(attr.getSlug()), 
                DataType.valueOf(attr.getType()),
                attr.is_global()
            ));
        });

        return map;
    }

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
