package microservice.cloud.inventory.category.infrastructure.adapter.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import microservice.cloud.inventory.attribute.domain.entity.AttributeDefinition;
import microservice.cloud.inventory.attribute.domain.value_objects.DataType;
import microservice.cloud.inventory.category.application.ports.out.CategoryReadRepository;
import microservice.cloud.inventory.category.domain.entity.Category;
import microservice.cloud.inventory.category.domain.entity.CategoryAttribute;
import microservice.cloud.inventory.category.infrastructure.entity.CategoryAttributeEntity;
import microservice.cloud.inventory.category.infrastructure.entity.CategoryEntity;
import microservice.cloud.inventory.shared.application.dto.Pagination;
import microservice.cloud.inventory.shared.domain.value_objects.Id;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;

@Repository
@RequiredArgsConstructor
public class CategoryReadRepositoryJpaImpl implements CategoryReadRepository {

    private final EntityManager entityManager;

    @Override
    public Pagination<Category> findAll(int page, int limit) {

        if (page < 1) {
            page = 1;
        }

        String jpqlData = "SELECT ca FROM CategoryEntity ca ORDER BY ca.id ASC";

        Query dataQuery = entityManager.createQuery(jpqlData, CategoryEntity.class);

        int firstResult = (page - 1) * limit;

        dataQuery.setFirstResult(firstResult);
        dataQuery.setMaxResults(limit);

        List<CategoryEntity> entities = dataQuery.getResultList();

        String jpqlCount = "SELECT COUNT(ca) FROM CategoryEntity ca";
        long totalItems = (Long) entityManager.createQuery(jpqlCount).getSingleResult();

        int totalPages = (int) Math.ceil((double) totalItems / limit);

        List<Category> domainObjects = entities.stream()
            .map(this::toDomain)
            .collect(Collectors.toList());

        return new Pagination<>(domainObjects, totalPages, page);
    }

    @Override
    public Category findById(Id id) {
        CategoryEntity entity = entityManager.find(CategoryEntity.class, id.value());

        if(entity == null)
            throw new RuntimeException("Category not found");

        return toDomain(entity);
    }

    private Category toDomain(CategoryEntity entity) {
        List<CategoryAttribute> categoryAttributes = 
            entity.getCategoryAttributes()
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());

        return new Category(
            new Id(entity.getId()), 
            entity.getName(), 
            new Slug(entity.getSlug()), 
            new Id(entity.getId()), 
            categoryAttributes
        );
    }

    private CategoryAttribute toDomain(CategoryAttributeEntity entity) {
        AttributeDefinition categoryAttributes = new AttributeDefinition(
            new Id(entity.getAttribute_definition().getId()), 
            entity.getAttribute_definition().getName(), 
            entity.getAttribute_definition().getSlug(), 
            DataType.valueOf(entity.getAttribute_definition().getType()),
            entity.getAttribute_definition().getIs_global().booleanValue()
        );

        return new CategoryAttribute(
            new Id(entity.getId()),
            categoryAttributes,
            entity.getIs_required(),
            entity.getIs_filterable(),
            entity.getIs_sortable()
        );
    }
}
