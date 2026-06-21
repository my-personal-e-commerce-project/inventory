package microservice.cloud.inventory.attribute.application.use_cases;

import microservice.cloud.inventory.attribute.application.ports.dto.AttributeDefinitionReadDTO;
import microservice.cloud.inventory.attribute.application.ports.out.AttributeDefinitionReadRepository;
import microservice.cloud.inventory.shared.application.dto.Pagination;

public class ListAttributeDefinitionUseCase {

    private final AttributeDefinitionReadRepository attributeDefinitionRepository;

    public ListAttributeDefinitionUseCase(
        AttributeDefinitionReadRepository attributeDefinitionRepository
    ) {
        this.attributeDefinitionRepository = attributeDefinitionRepository;
    }

    public Pagination<AttributeDefinitionReadDTO> execute(int page, int limit) {
        return attributeDefinitionRepository.findAll(page, limit);
    }
}
