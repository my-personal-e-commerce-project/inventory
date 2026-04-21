package microservice.cloud.inventory.attribute.application.ports.in;

import microservice.cloud.inventory.attribute.domain.value_objects.DataType;
import microservice.cloud.inventory.shared.domain.value_objects.Id;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;

public interface CreateAttributeDefinitionUseCasePort {

    public void execute(
        Id id, 
        String name, 
        Slug slug, 
        DataType type, 
        boolean is_global
    );
}
