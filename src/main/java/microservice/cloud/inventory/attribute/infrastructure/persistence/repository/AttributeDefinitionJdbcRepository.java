package microservice.cloud.inventory.attribute.infrastructure.persistence.repository;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import microservice.cloud.inventory.attribute.infrastructure.persistence.model.AttributeDefinitionEntity;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public interface AttributeDefinitionJdbcRepository extends PagingAndSortingRepository<AttributeDefinitionEntity, String> {

    record SlugIdProjection(String slug, String id) {}

    @Query("SELECT slug, id FROM attributedefinition WHERE slug IN (:slugs)")
    List<SlugIdProjection> _findIdsBySlugsInternal(@Param("slugs") Collection<String> slugs);

    default Map<String, String> findIdsBySlugs(Collection<String> slugs) {
        if (slugs == null || slugs.isEmpty()) {
            return Collections.emptyMap();
        }

        return _findIdsBySlugsInternal(slugs)
                .stream()
                .collect(Collectors.toMap(
                    SlugIdProjection::slug, 
                    SlugIdProjection::id,
                    (existing, replacement) -> existing
                ));
    }
}
