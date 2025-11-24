package microservice.cloud.inventory.attribute.domain.repository;

import microservice.cloud.inventory.attribute.domain.entity.AttributeDefinition;
import microservice.cloud.inventory.shared.domain.value_objects.Id;

public interface AttributeDefinitionRepository {

    public AttributeDefinition getByCategoryAttributeId(Id categoryAttributeId);
    public void deleteByCategoryAttributeId(Id categoryAttributeId);
    public AttributeDefinition getByProductAttributeId(Id categoryAttributeId);
}
