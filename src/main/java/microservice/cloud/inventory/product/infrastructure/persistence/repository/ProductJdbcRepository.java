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
        DELETE FROM product_attribute_values pav
        WHERE pav.attribute_definition_id = :attributeId
          AND pav.product_id IN (
              SELECT pc.product_id 
              FROM product_categories pc 
              WHERE pc.category_id = :categoryId
          )
          AND NOT EXISTS (
              SELECT 1 
              FROM product_categories pc_other
              JOIN CategoryAttribute ca ON pc_other.category_id = ca.category_id
              WHERE pc_other.product_id = pav.product_id
                AND ca.attribute_definition_id = :attributeId
          )
    """)
    void deleteOrphanAttributeValues(
        @Param("attributeId") String attributeId, 
        @Param("categoryId") String categoryId
    );

    @Modifying
    @Query("""
        INSERT INTO product_attribute_values (
                id, attribute_definition_id, product_id, 
                string_value, integer_value, double_value, boolean_value
            )
            SELECT 
                gen_random_uuid()::text, :attributeId, id, 
                :string_value, :integer_value, :double_value, :boolean_value
            FROM products
            ON CONFLICT (product_id, attribute_definition_id) 
            DO NOTHING
    """)
    void massCreateDefaultProductAttributeValues(
        @Param("attributeId") String attributeId, 
        @Param("string_value") String string_value,
        @Param("integer_value") Integer integer_value,
        @Param("double_value") Double double_value,
        @Param("boolean_value") Boolean boolean_value
    );

    boolean existsBySlug(String slug);
}
