package microservice.cloud.inventory.attribute.application.ports.out;

import microservice.cloud.inventory.attribute.application.ports.dto.AttributeDefinitionReadDTO;
import microservice.cloud.inventory.attribute.application.ports.dto.QueryAttributeDefinitions;
import microservice.cloud.inventory.shared.application.dto.Pagination;

public interface AttributeDefinitionReadRepository {

    public Pagination<AttributeDefinitionReadDTO> findAll(QueryAttributeDefinitions query, int page, int limit);
}
