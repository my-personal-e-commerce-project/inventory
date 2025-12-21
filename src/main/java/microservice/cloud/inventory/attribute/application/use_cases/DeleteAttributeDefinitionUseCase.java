package microservice.cloud.inventory.attribute.application.use_cases;

import microservice.cloud.inventory.attribute.application.ports.in.DeleteAttributeDefinitionUseCasePort;
import microservice.cloud.inventory.attribute.domain.entity.AttributeDefinition;
import microservice.cloud.inventory.attribute.domain.repository.AttributeDefinitionRepository;
import microservice.cloud.inventory.shared.domain.value_objects.Id;

public class DeleteAttributeDefinitionUseCase implements DeleteAttributeDefinitionUseCasePort{

    private AttributeDefinitionRepository attributeDefinitionRepository;

    public DeleteAttributeDefinitionUseCase(
        AttributeDefinitionRepository attributeDefinitionRepository
    ) {
        this.attributeDefinitionRepository = attributeDefinitionRepository;
    }

    @Override
    public void execute(Id id) {
        attributeDefinitionRepository.delete(id);
    } 
}
