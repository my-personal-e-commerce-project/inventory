package microservice.cloud.inventory.attribute.application.ports.in;

import microservice.cloud.inventory.attribute.domain.entity.AttributeDefinition;
import microservice.cloud.inventory.attribute.domain.value_objects.DataType;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;

public interface UpdateAttributeDefinitionUseCasePort {

    public AttributeDefinition execute(
        Slug find_slug,
        String name,
        Slug slug,
        DataType type,
        boolean is_global
    );
}
