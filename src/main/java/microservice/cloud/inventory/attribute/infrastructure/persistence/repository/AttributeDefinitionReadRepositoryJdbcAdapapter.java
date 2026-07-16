package microservice.cloud.inventory.attribute.infrastructure.persistence.repository;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import microservice.cloud.inventory.attribute.application.ports.dto.AttributeDefinitionReadDTO;
import microservice.cloud.inventory.attribute.application.ports.dto.QueryAttributeDefinitions;
import microservice.cloud.inventory.attribute.application.ports.out.AttributeDefinitionReadRepository;
import microservice.cloud.inventory.attribute.infrastructure.persistence.model.AttributeDefinitionEntity;
import microservice.cloud.inventory.shared.application.dto.Pagination;

@Repository
@RequiredArgsConstructor
public class AttributeDefinitionReadRepositoryJdbcAdapapter implements AttributeDefinitionReadRepository {

    private final JdbcTemplate jdbcTemplate;
    private final AttributeDefinitionJdbcRepository attributeDefinitionJdbcRepository;

    @Override
    public Pagination<AttributeDefinitionReadDTO> findAll(QueryAttributeDefinitions query, int page, int limit) {
        List<AttributeDefinitionEntity> result = attributeDefinitionJdbcRepository
            .findAllAndSearch(query.search(), limit, page);

        long total = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM products;", Long.class);

        int totalPages = (limit == 0) ? 1 : (int) Math.ceil((double) total / limit);
    
        int last_page = Math.max(0, totalPages - 1);

        return new Pagination<AttributeDefinitionReadDTO>(
            result
                .stream()
                .map(
                    (attr) -> toMap(attr)
                ).toList(),
            page,
            last_page
        );
    }

    private AttributeDefinitionReadDTO toMap(AttributeDefinitionEntity entity) {
        return new AttributeDefinitionReadDTO(
            entity.getId(),
            entity.getName(),
            entity.getSlug(),
            entity.getType(),
            Boolean.valueOf(entity.isGlobal())
        );
    }
}
