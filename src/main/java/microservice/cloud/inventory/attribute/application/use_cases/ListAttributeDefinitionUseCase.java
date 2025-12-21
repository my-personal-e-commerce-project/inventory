package microservice.cloud.inventory.attribute.application.use_cases;

import microservice.cloud.inventory.attribute.application.ports.dto.AttributeDefinitionReadDTO;
import microservice.cloud.inventory.attribute.application.ports.in.ListAttributeDefinitionUseCasePort;
import microservice.cloud.inventory.attribute.application.ports.out.AttributeDefinitionReadRepository;
import microservice.cloud.inventory.shared.application.dto.Pagination;

public class ListAttributeDefinitionUseCase implements ListAttributeDefinitionUseCasePort {

    private final AttributeDefinitionReadRepository attributeDefinitionRepository;

    public ListAttributeDefinitionUseCase(
        AttributeDefinitionReadRepository attributeDefinitionRepository
    ) {
        this.attributeDefinitionRepository = attributeDefinitionRepository;
    }

    @Override
    public Pagination<AttributeDefinitionReadDTO> execute(int page, int limit) {
        return attributeDefinitionRepository.findAll(page, limit);
    }
}
