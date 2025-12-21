package microservice.cloud.inventory.attribute.application.ports.in;

import microservice.cloud.inventory.shared.domain.value_objects.Id;

public interface DeleteAttributeDefinitionUseCasePort {
    
    public void execute(Id id);
}
