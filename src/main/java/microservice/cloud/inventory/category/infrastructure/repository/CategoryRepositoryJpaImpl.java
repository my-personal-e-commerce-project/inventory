package microservice.cloud.inventory.category.infrastructure.repository;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import microservice.cloud.inventory.attribute.infrastructure.entity.AttributeDefinitionEntity;
import microservice.cloud.inventory.category.domain.entity.Category;
import microservice.cloud.inventory.category.domain.entity.CategoryAttribute;
import microservice.cloud.inventory.category.domain.repository.CategoryRepository;
import microservice.cloud.inventory.category.infrastructure.entity.CategoryAttributeEntity;
import microservice.cloud.inventory.category.infrastructure.entity.CategoryEntity;
import microservice.cloud.inventory.shared.domain.value_objects.Id;

@Repository
@RequiredArgsConstructor
public class CategoryRepositoryJpaImpl implements CategoryRepository {

    private final EntityManager entityManager;

    @Override
    public void save(Category category) {
        entityManager.persist(CategoryEntity.builder()
            .id(category.id().value())
            .name(category.name())
            .slug(category.slug().value())
            .parent(entityManager.find(CategoryEntity.class, category.parent_id().value()))
            .build()
        );
    }

    @Override
    public void update(Category category) {
        entityManager.persist(CategoryEntity.builder()
            .id(category.id().value())
            .name(category.name())
            .slug(category.slug().value())
            .parent(entityManager.find(CategoryEntity.class, category.parent_id().value()))
            .build()
        );        
    }

    @Override
    public void syncCategoryAttributes(Category category) {
        CategoryEntity entity = entityManager.find(
                CategoryEntity.class, 
                category.id().value()
            );

        entity.getCategoryAttributes().clear();

        category.categoryAttributes().stream().forEach(data -> {
            entity.getCategoryAttributes().add(
                CategoryAttributeEntity.builder()
                    .id(data.id().value())
                    .attribute_definition(entityManager.find(AttributeDefinitionEntity.class, data.attribute_definition().id().value()))
                    .is_required(data.is_required())
                    .is_filterable(data.is_filterable())
                    .is_sortable(data.is_sortable())
                    .build()
            );
        });

        entityManager.merge(entity);
    }

    @Override
    public void delete(Id id) {
        entityManager.remove(id.value());
    }
}
