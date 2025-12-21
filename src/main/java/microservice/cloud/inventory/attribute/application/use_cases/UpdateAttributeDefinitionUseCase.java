package microservice.cloud.inventory.attribute.application.use_cases;

import microservice.cloud.inventory.attribute.application.ports.in.UpdateAttributeDefinitionUseCasePort;
import microservice.cloud.inventory.attribute.domain.entity.AttributeDefinition;
import microservice.cloud.inventory.attribute.domain.repository.AttributeDefinitionRepository;

public class UpdateAttributeDefinitionUseCase implements UpdateAttributeDefinitionUseCasePort {

    private AttributeDefinitionRepository attributeDefinitionRepository;

    public UpdateAttributeDefinitionUseCase(
        AttributeDefinitionRepository attributeDefinitionRepository
    ) {
        this.attributeDefinitionRepository = attributeDefinitionRepository;
    }

    @Override
    public AttributeDefinition execute(AttributeDefinition attr) {
        attributeDefinitionRepository.update(attr);
        return attr;
    } 
}
