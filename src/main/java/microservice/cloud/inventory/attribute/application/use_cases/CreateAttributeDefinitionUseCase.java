package microservice.cloud.inventory.attribute.application.use_cases;

import microservice.cloud.inventory.attribute.application.ports.in.CreateAttributeDefinitionUseCasePort;
import microservice.cloud.inventory.attribute.domain.entity.AttributeDefinition;
import microservice.cloud.inventory.attribute.domain.repository.AttributeDefinitionRepository;

public class CreateAttributeDefinitionUseCase implements CreateAttributeDefinitionUseCasePort {

    private AttributeDefinitionRepository attributeDefinitionRepository;

    public CreateAttributeDefinitionUseCase(
        AttributeDefinitionRepository attributeDefinitionRepository
    ) {
        this.attributeDefinitionRepository = attributeDefinitionRepository;
    }

    @Override
    public AttributeDefinition execute(AttributeDefinition attr) {
        attributeDefinitionRepository.save(attr);

        return attr;
    }
}
