package microservice.cloud.inventory.attribute.infrastructure.persistence.repository;

import java.util.List;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import microservice.cloud.inventory.attribute.infrastructure.persistence.model.AttributeDefinitionEntity;

@Repository
public interface AttributeDefinitionJdbcRepository extends 
    ListCrudRepository<AttributeDefinitionEntity, String>, 
    PagingAndSortingRepository<AttributeDefinitionEntity, String> 
{
    List<AttributeDefinitionEntity> findAllByIdIn(List<String> ids);
    long countByIdIn(List<String> ids);
    boolean existsBySlug(String slug);
}
