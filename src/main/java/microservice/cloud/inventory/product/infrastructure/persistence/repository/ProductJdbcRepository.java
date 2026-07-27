package microservice.cloud.inventory.product.infrastructure.persistence.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import microservice.cloud.inventory.product.infrastructure.persistence.entity.ProductEntity;

public interface ProductJdbcRepository extends CrudRepository<ProductEntity, String> {

    Optional<ProductEntity> findById(String id);

    Optional<ProductEntity> findBySlug(String slug);
   
    boolean existsBySlug(String slug);
}
