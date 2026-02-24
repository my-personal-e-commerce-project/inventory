package microservice.cloud.inventory.attribute.application.use_cases;

import microservice.cloud.inventory.attribute.application.ports.in.DeleteAttributeDefinitionUseCasePort;
import microservice.cloud.inventory.attribute.domain.entity.AttributeDefinition;
import microservice.cloud.inventory.attribute.domain.repository.AttributeDefinitionRepository;
import microservice.cloud.inventory.shared.application.ports.out.GetMePort;
import microservice.cloud.inventory.shared.domain.value_objects.Id;

public class DeleteAttributeDefinitionUseCase implements DeleteAttributeDefinitionUseCasePort{

    private AttributeDefinitionRepository attributeDefinitionRepository;
    private GetMePort getMePort;
        
    public DeleteAttributeDefinitionUseCase(
        AttributeDefinitionRepository attributeDefinitionRepository,
        GetMePort getMePort
    ) {
        this.attributeDefinitionRepository = attributeDefinitionRepository;
        this.getMePort = getMePort;
    }

    @Override
    public void execute(Id id) {
        AttributeDefinition.delete(getMePort.execute());
        attributeDefinitionRepository.delete(id);
    } 
}
