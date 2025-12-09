package microservice.cloud.inventory.category.infrastructure.repository;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import microservice.cloud.inventory.attribute.domain.entity.AttributeDefinition;
import microservice.cloud.inventory.attribute.domain.value_objects.DataType;
import microservice.cloud.inventory.category.application.dtos.AttributeDefinitionReadDTO;
import microservice.cloud.inventory.category.application.dtos.CategoryAttributeReadDTO;
import microservice.cloud.inventory.category.application.dtos.CategoryReadDTO;
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
    public Pagination<CategoryReadDTO> findAll(int page, int limit) {

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

        List<CategoryReadDTO> domainObjects = entities.stream()
            .map(this::toDomain)
            .collect(Collectors.toList());

        return new Pagination<CategoryReadDTO>(domainObjects, totalPages, page);
    }

    @Override
    public CategoryReadDTO findById(Id id) {
        CategoryEntity entity = entityManager.find(CategoryEntity.class, id.value());
        if(entity == null)
            throw new RuntimeException("Category not found");

        return toDomain(entity);
    }

    private CategoryReadDTO toDomain(CategoryEntity entity) {
        List<CategoryAttributeReadDTO> categoryAttributes = 
            entity.getCategoryAttributes()
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());

        return new CategoryReadDTO(
            entity.getId(), 
            entity.getName(), 
            entity.getSlug(),
            entity.getParent() == null? null: entity.getParent().getId(),
            categoryAttributes
        );
    }

    private CategoryAttributeReadDTO toDomain(CategoryAttributeEntity entity) {
        AttributeDefinitionReadDTO categoryAttributes = new AttributeDefinitionReadDTO(
            entity.getAttribute_definition().getId(), 
            entity.getAttribute_definition().getName(), 
            entity.getAttribute_definition().getSlug(),
            entity.getAttribute_definition().getType(),
            entity.getAttribute_definition().is_global()
        );

        return new CategoryAttributeReadDTO(
            entity.getId(),
            categoryAttributes,
            entity.getIs_required(),
            entity.getIs_filterable(),
            entity.getIs_sortable()
        );
    }
}
