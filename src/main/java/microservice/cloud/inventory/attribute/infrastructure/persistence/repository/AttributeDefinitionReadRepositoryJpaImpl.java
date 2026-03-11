package microservice.cloud.inventory.attribute.infrastructure.persistence.repository;

import org.springframework.stereotype.Repository;

import microservice.cloud.inventory.attribute.application.ports.dto.AttributeDefinitionReadDTO;
import microservice.cloud.inventory.attribute.application.ports.out.AttributeDefinitionReadRepository;
import microservice.cloud.inventory.shared.application.dto.Pagination;

@Repository
public class AttributeDefinitionReadRepositoryJpaImpl implements AttributeDefinitionReadRepository {

    @Override
    public Pagination<AttributeDefinitionReadDTO> findAll(int page, int limit) {
        return null;
    }
}
