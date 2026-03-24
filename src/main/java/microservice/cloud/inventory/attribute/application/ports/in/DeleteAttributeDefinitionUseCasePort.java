package microservice.cloud.inventory.attribute.application.ports.in;

import microservice.cloud.inventory.shared.domain.value_objects.Slug;

public interface DeleteAttributeDefinitionUseCasePort {
    
    public void execute(Slug find_slug);
}
