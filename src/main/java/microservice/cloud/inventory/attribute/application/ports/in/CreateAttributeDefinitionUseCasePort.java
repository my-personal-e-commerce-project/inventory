package microservice.cloud.inventory.attribute.application.ports.in;

import microservice.cloud.inventory.attribute.domain.entity.AttributeDefinition;

public interface CreateAttributeDefinitionUseCasePort {

    public void execute(
        AttributeDefinition attributeDefinition
    );
}
