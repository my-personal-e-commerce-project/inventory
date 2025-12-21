package microservice.cloud.inventory.category.infrastructure.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;
import microservice.cloud.inventory.attribute.domain.entity.AttributeDefinition;
import microservice.cloud.inventory.attribute.domain.value_objects.DataType;
import microservice.cloud.inventory.attribute.infrastructure.persistence.model.AttributeDefinitionEntity;
import microservice.cloud.inventory.category.domain.entity.Category;
import microservice.cloud.inventory.category.domain.entity.CategoryAttribute;
import microservice.cloud.inventory.category.domain.repository.CategoryRepository;
import microservice.cloud.inventory.category.infrastructure.entity.CategoryAttributeEntity;
import microservice.cloud.inventory.category.infrastructure.entity.CategoryEntity;
import microservice.cloud.inventory.shared.domain.value_objects.Id;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;

@Repository
@RequiredArgsConstructor
public class CategoryRepositoryJpaImpl implements CategoryRepository {

    private final EntityManager entityManager;

    @Override
    public Category findById(Id id) {
        CategoryEntity entity = entityManager
            .find(CategoryEntity.class, id.value());

        if(entity == null)
            throw new EntityNotFoundException("Category not found");

        return toMap(entity);
    }

    @Override
    public List<CategoryAttribute> getCategoryAttributesByCategoryIds(List<String> categoriesIds) {
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

        List<CategoryAttributeEntity> results = new ArrayList<>();

        categories
            .stream()
            .forEach((c)->{
                results.addAll(
                    c.getCategoryAttributes()
                );
            });

        return results
            .stream()
            .map(a->toMap(a))
            .toList();
    }

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

        category.categoryAttributes().stream().forEach(data -> {
            entity.getCategoryAttributes().add(
                CategoryAttributeEntity.builder()
                    .id(data.id().value())
                    .attribute_definition(
                        AttributeDefinitionEntity.builder()
                            .id(data.attribute_definition().id().value())
                            .name(data.attribute_definition().name())
                            .slug(data.attribute_definition().slug().value())
                            .type(data.attribute_definition().type().toString())
                            .is_global(data.attribute_definition().is_global())
                            .build() 
                    )
                    .is_required(data.is_required())
                    .is_filterable(data.is_filterable())
                    .is_sortable(data.is_sortable())
                    .category(entity)
                    .build()
            );
        });

        entityManager.persist(entity);
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
                        AttributeDefinitionEntity.builder()
                            .id(data.attribute_definition().id().value())
                            .name(data.attribute_definition().name())
                            .slug(data.attribute_definition().slug().value())
                            .type(data.attribute_definition().type().toString())
                            .is_global(data.attribute_definition().is_global())
                            .build() 
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

    @Transactional
    @Override
    public void delete(Category category) {
        CategoryEntity entity = entityManager.find(CategoryEntity.class, category.id().value());
        entityManager.remove(entity);
    }

    private boolean existBySlug(String slug) {
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

    private boolean existByName(String name) {
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

    private CategoryAttribute toMap(CategoryAttributeEntity attr) {
        return new CategoryAttribute(
            new Id(attr.getId()), 
            new AttributeDefinition(
                new Id(attr.getAttribute_definition().getId()), 
                attr.getAttribute_definition().getName(), 
                new Slug(attr.getAttribute_definition().getSlug()), 
                DataType.valueOf(attr.getAttribute_definition().getType()), 
                false
            ),
            attr.getIs_required(), 
            attr.getIs_filterable(), 
            attr.getIs_sortable()
        );
    }

    private Category toMap(CategoryEntity category) {
        List<CategoryAttribute> attrs = category.getCategoryAttributes()
            .stream()
            .map(attr -> toMap(attr))
            .toList();

        return new Category(
            new Id(category.getId()),
            category.getName(),
            new Slug(category.getSlug()),
            category.getParent() == null? null: new Id(category.getParent().getId()),
            attrs
        );
    }
}
