package microservice.cloud.inventory.category.infrastructure.persistence.repository;

import org.springframework.data.repository.CrudRepository;

import microservice.cloud.inventory.category.infrastructure.persistence.model.CategoryEntity;

public interface CategoryJdbcRepository extends CrudRepository<CategoryEntity, String> {

   
    boolean existsByName(String name);
    boolean existsBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, String id);
    boolean existsByNameAndIdNot(String name, String id);
}
