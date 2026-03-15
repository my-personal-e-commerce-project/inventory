package microservice.cloud.inventory.category.infrastructure.persistence.repository;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.repository.CrudRepository;

import microservice.cloud.inventory.category.infrastructure.persistence.model.CategoryEntity;

public interface CategoryJdbcRepository extends CrudRepository<CategoryEntity, String> {

    Optional<CategoryEntity> findBySlug(String slug);

    boolean countByIdIn(Set<String> ids);

    boolean existsByName(String name);
    boolean existsBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, String id);
    boolean existsByNameAndIdNot(String name, String id);
}
