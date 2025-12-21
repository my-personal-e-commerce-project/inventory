package microservice.cloud.inventory.attribute.application.ports.in;

import microservice.cloud.inventory.attribute.domain.entity.AttributeDefinition;

public interface UpdateAttributeDefinitionUseCasePort {

    public AttributeDefinition execute(AttributeDefinition attr);
}
