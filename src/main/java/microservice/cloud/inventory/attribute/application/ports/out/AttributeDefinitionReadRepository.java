package microservice.cloud.inventory.attribute.application.ports.out;

import microservice.cloud.inventory.attribute.application.ports.dto.AttributeDefinitionReadDTO;
import microservice.cloud.inventory.shared.application.dto.Pagination;

public interface AttributeDefinitionReadRepository {

    public Pagination<AttributeDefinitionReadDTO> findAll(int page, int limit);
}
