package microservice.cloud.inventory.attribute.infrastructure.persistence.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import microservice.cloud.inventory.attribute.application.ports.dto.AttributeDefinitionReadDTO;
import microservice.cloud.inventory.attribute.application.ports.out.AttributeDefinitionReadRepository;
import microservice.cloud.inventory.attribute.infrastructure.persistence.model.AttributeDefinitionEntity;
import microservice.cloud.inventory.shared.application.dto.Pagination;

@Repository
@RequiredArgsConstructor
public class AttributeDefinitionReadRepositoryJdbcAdapapter implements AttributeDefinitionReadRepository {

    private final AttributeDefinitionJdbcRepository attributeDefinitionJdbcRepository;

    @Override
    public Pagination<AttributeDefinitionReadDTO> findAll(int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit);

        Page<AttributeDefinitionEntity> result = attributeDefinitionJdbcRepository
            .findAll(pageable);

        return new Pagination<AttributeDefinitionReadDTO>(
            result.getContent()
                .stream()
                .map(
                    (attr) -> toMap(attr)
                ).toList(),
            page,
            result.getTotalPages()
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
