package microservice.cloud.inventory.product.infrastructure.persistence.repository;

import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import microservice.cloud.inventory.product.infrastructure.persistence.entity.ProductEntity;

public interface ProductJdbcRepository extends CrudRepository<ProductEntity, String> {

    Optional<ProductEntity> findBySlug(String slug);
    
    @Modifying
    @Query("""
        DELETE FROM product_attribute_values
        WHERE attribute_definition_id = :attributeId
          AND product_id IN (
              SELECT pc.product_id 
              FROM product_categories pc 
              WHERE pc.category_id = :categoryId
          )
          AND NOT EXISTS (
              SELECT 1 
              FROM product_categories pc_other
              JOIN categoryattribute ca ON pc_other.category_id = ca.category_id
              WHERE pc_other.product_id = product_attribute_values.product_id
                AND pc_other.category_id != :categoryId
                AND ca.attribute_definition_id = :attributeId
          )
    """)
    void deleteOrphanAttributeValues(
        @Param("attributeId") String attributeId, 
        @Param("categoryId") String categoryId
    );
}
