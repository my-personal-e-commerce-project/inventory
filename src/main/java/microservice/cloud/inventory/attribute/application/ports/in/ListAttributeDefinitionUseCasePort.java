package microservice.cloud.inventory.attribute.application.ports.in;

import microservice.cloud.inventory.attribute.application.ports.dto.AttributeDefinitionReadDTO;
import microservice.cloud.inventory.shared.application.dto.Pagination;

public interface ListAttributeDefinitionUseCasePort {

   public Pagination<AttributeDefinitionReadDTO> execute(int page, int limit);
}
