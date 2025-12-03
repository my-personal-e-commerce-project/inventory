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
import microservice.cloud.inventory.category.infrastructure.entity.CategoryEntity;
import microservice.cloud.inventory.shared.domain.value_objects.Id;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;

@Repository
@RequiredArgsConstructor
public class AttributeDefinitionRepositoryJpaImpl implements AttributeDefinitionRepository {

    private final EntityManager entityManager;

    @Override
    public List<AttributeDefinition> getByCategoryAttributeIds(List<String> ids) {
        List<String> categoriesIds = new ArrayList<>(ids);

        List<CategoryEntity> categories = entityManager.createQuery(
                "SELECT a FROM CategoryEntity a WHERE a.id IN :ids", CategoryEntity.class
            )
            .setParameter("ids", categoriesIds)
            .getResultList();

        if (categories.size() != categories.size()) {
            Set<String> foundIds = categories.stream()
                .map(CategoryEntity::getId)
                .collect(Collectors.toSet());
            categoriesIds.removeAll(foundIds);
            throw new EntityNotFoundException("Categories not found: " + categoriesIds);
        }

        List<AttributeDefinitionEntity> results = new ArrayList<>();

        categories
            .stream()
            .forEach((c)->{
                results.addAll(
                    c.getCategoryAttributes()
                        .stream()
                        .map(a->a.getAttribute_definition())
                        .toList()
                );
            });

        return results
            .stream()
            .map(a->toMap(a))
            .toList();
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
