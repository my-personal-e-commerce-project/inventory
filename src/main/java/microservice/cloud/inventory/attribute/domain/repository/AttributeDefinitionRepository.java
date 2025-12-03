package microservice.cloud.inventory.attribute.domain.repository;

import java.util.List;

import microservice.cloud.inventory.attribute.domain.entity.AttributeDefinition;
import microservice.cloud.inventory.shared.domain.value_objects.Id;

public interface AttributeDefinitionRepository {

    public List<AttributeDefinition> getByCategoryAttributeIds(List<String> ids);
    public void deleteByCategoryAttributeId(Id categoryAttributeId);
    public AttributeDefinition getByProductAttributeId(Id categoryAttributeId);
}
