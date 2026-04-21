package microservice.cloud.inventory.attribute.application.ports.out;

import microservice.cloud.inventory.attribute.domain.entity.AttributeDefinition;

public interface AsynchronousBulkCreationOfDefaultValuesForProductAttributes {

    public void execute(AttributeDefinition attrDef);
}
