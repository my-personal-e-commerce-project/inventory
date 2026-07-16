package microservice.cloud.inventory.attribute.infrastructure.persistence.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import microservice.cloud.inventory.attribute.infrastructure.persistence.model.AttributeDefinitionEntity;

@Repository
public interface AttributeDefinitionJdbcRepository extends 
    ListCrudRepository<AttributeDefinitionEntity, String>, 
    PagingAndSortingRepository<AttributeDefinitionEntity, String> 
{
    @Query("""
        SELECT * FROM AttributeDefinition a 
        WHERE (:search IS NULL OR :search = '')
           OR (a.name ILIKE '%' || :search || '%' OR a.slug ILIKE '%' || :search || '%')
        ORDER BY a.name
        LIMIT :limit OFFSET :offset
    """)
    List<AttributeDefinitionEntity> findAllAndSearch(
        @Param("search") String search, 
        @Param("limit") int limit, 
        @Param("offset") long offset
    );

    AttributeDefinitionEntity findBySlug(String slug);
    
    List<AttributeDefinitionEntity> findAllByIdIn(Set<String> ids);
    List<AttributeDefinitionEntity> findAllByIdIn(List<String> ids);
    
    List<AttributeDefinitionEntity> findAllByIsGlobal(boolean is_global);
   
    public long countByIdIn(Set<String> ids);
    boolean existsBySlug(String slug);
}
