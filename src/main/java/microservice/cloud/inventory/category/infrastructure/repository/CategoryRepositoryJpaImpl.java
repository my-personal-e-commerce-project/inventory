package microservice.cloud.inventory.category.infrastructure.repository;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;

import microservice.cloud.inventory.attribute.infrastructure.entity.AttributeDefinitionEntity;
import microservice.cloud.inventory.category.domain.entity.Category;
import microservice.cloud.inventory.category.domain.repository.CategoryRepository;
import microservice.cloud.inventory.category.infrastructure.entity.CategoryAttributeEntity;
import microservice.cloud.inventory.category.infrastructure.entity.CategoryEntity;
import microservice.cloud.inventory.shared.domain.value_objects.Id;

@Repository
@RequiredArgsConstructor
public class CategoryRepositoryJpaImpl implements CategoryRepository {

    private final EntityManager entityManager;

    @Transactional
    @Override
    public void save(Category category) {
        if(existBySlug(category.slug().value())){
            throw new RuntimeException("Category with slug " + category.slug().value() + " already exists");
        }

        if(existByName(category.name())){
            throw new RuntimeException("Category with name " + category.name() + " already exists");
        }
        
        CategoryEntity entity = CategoryEntity.builder()
            .id(category.id().value())
            .name(category.name())
            .slug(category.slug().value())
            .build();
    
        if(category.parent_id() != null){
            CategoryEntity parent = entityManager.find(CategoryEntity.class, category.parent_id().value());
            entity.setParent(parent);
        }

        entityManager.persist(entity);
        
        syncCategoryAttributes(category);
    }

    @Transactional
    @Override
    public void update(Category category) {
        CategoryEntity entity = entityManager.find(
            CategoryEntity.class, 
            category.id().value()
        );

        if(entity == null) {
            throw new EntityNotFoundException("Category not found");
        }

        entity.setName(category.name());
        entity.setSlug(category.slug().value());

        if(category.parent_id() != null){
            CategoryEntity parent = entityManager.find(CategoryEntity.class, category.parent_id().value());
            entity.setParent(parent);
        }

        syncCategoryAttributes(category);
    }

    private void syncCategoryAttributes(Category category) {
        if(category.categoryAttributes() == null)
            return;

        CategoryEntity entity = entityManager.find(
                CategoryEntity.class, 
                category.id().value()
            );

        entity.getCategoryAttributes().clear();

        category.categoryAttributes().stream().forEach(data -> {
            entity.getCategoryAttributes().add(
                CategoryAttributeEntity.builder()
                    .id(data.id().value())
                    .attribute_definition(
                        new AttributeDefinitionEntity(
                            data.attribute_definition().id().value(),
                            data.attribute_definition().name(),
                            data.attribute_definition().slug().value(),
                            data.attribute_definition().type().toString(),
                            data.attribute_definition().is_global()
                        )
                    )
                    .is_required(data.is_required())
                    .is_filterable(data.is_filterable())
                    .is_sortable(data.is_sortable())
                    .category(entity)
                    .build()
            );
        });

        entityManager.merge(entity);
    }

    public boolean existBySlug(String slug) {
        String jpql = "SELECT COUNT(c) FROM CategoryEntity c WHERE c.slug = :slug";

        try {
            Long count = entityManager.createQuery(jpql, Long.class)
                .setParameter("slug", slug)
                .getSingleResult(); 
            
            return count > 0;
        } catch (NoResultException e) {
            return false;
        }
    }

    public boolean existByName(String name) {
        String jpql = "SELECT COUNT(c) FROM CategoryEntity c WHERE c.name = :name";

        try {
            Long count = entityManager.createQuery(jpql, Long.class)
                .setParameter("name", name)
                .getSingleResult(); 
            
            return count > 0;
        } catch (NoResultException e) {
            return false;
        }
    }

    @Override
    public void delete(Id id) {
        entityManager.remove(id.value());
    }
}
