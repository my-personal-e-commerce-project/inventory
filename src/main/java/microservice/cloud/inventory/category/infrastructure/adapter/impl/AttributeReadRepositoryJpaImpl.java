package microservice.cloud.inventory.category.infrastructure.adapter.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import microservice.cloud.inventory.category.application.ports.out.AttributeReadRepository;
import microservice.cloud.inventory.category.domain.entity.CategoryAttribute;
import microservice.cloud.inventory.category.infrastructure.entity.CategoryAttributeEntity;
import microservice.cloud.inventory.shared.application.dto.Pagination;
import microservice.cloud.inventory.shared.domain.value_objects.Id;
import microservice.cloud.inventory.attribute.domain.value_objects.DataType;
import microservice.cloud.inventory.attribute.domain.entity.AttributeDefinition;

@Repository
@RequiredArgsConstructor
public class AttributeReadRepositoryJpaImpl implements AttributeReadRepository {

    private final EntityManager entityManager;

    @Override
    public Pagination<CategoryAttribute> findAll(int page, int limit) {
        String jpqlData = "SELECT ca FROM CategoryAttributeEntity ca ORDER BY ca.id ASC"; 

        Query dataQuery = entityManager.createQuery(jpqlData, CategoryAttributeEntity.class);

        int firstResult = (page - 1) * limit;

        dataQuery.setFirstResult(firstResult);
        dataQuery.setMaxResults(limit);

        List<CategoryAttributeEntity> entities = dataQuery.getResultList();

        String jpqlCount = "SELECT COUNT(ca) FROM CategoryAttributeEntity ca";
        long totalItems = (Long) entityManager.createQuery(jpqlCount).getSingleResult();

        List<CategoryAttribute> domainObjects = entities.stream()
            .map(this::toDomain)
            .collect(Collectors.toList());

        return new Pagination<CategoryAttribute>(domainObjects, (Integer) page, (Integer) limit, null);
    }

    private CategoryAttribute toDomain(CategoryAttributeEntity entity) {
        AttributeDefinition attributeDefinition = new AttributeDefinition(
            new Id(entity.getAttribute_definition().getId()),
            entity.getAttribute_definition().getName(),
            entity.getAttribute_definition().getSlug(),
            DataType.valueOf(entity.getAttribute_definition().getType()),
            entity.getAttribute_definition().getIs_global().booleanValue()
        );

        return new CategoryAttribute(
            new Id(entity.getId()), 
            attributeDefinition,
            entity.getIs_required(), 
            entity.getIs_filterable(), 
            entity.getIs_sortable()
        );
    }
}
