package microservice.cloud.inventory.product.infrastructure.persistence.repository;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import microservice.cloud.inventory.product.infrastructure.persistence.entity.ProductAttributeValueEntity;

@Repository
public interface ProductAttributeValueJdbcRepository extends CrudRepository<ProductAttributeValueEntity, String> {

    @Query("SELECT * FROM product_attribute_values WHERE id = :id")
    ProductAttributeValueEntity findByAttributeDefinitionId(@Param("id") String id);
}
