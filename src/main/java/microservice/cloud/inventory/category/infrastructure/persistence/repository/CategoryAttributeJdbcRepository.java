package microservice.cloud.inventory.category.infrastructure.persistence.repository;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import microservice.cloud.inventory.category.infrastructure.persistence.model.CategoryAttributeEntity;

@Repository
public interface CategoryAttributeJdbcRepository extends CrudRepository<CategoryAttributeEntity, String> {

    @Query("SELECT * FROM categoryattribute WHERE attribute_definition_id = :id")
    public CategoryAttributeEntity findByAttributeDefinitionId(@Param("id") String id);
}
