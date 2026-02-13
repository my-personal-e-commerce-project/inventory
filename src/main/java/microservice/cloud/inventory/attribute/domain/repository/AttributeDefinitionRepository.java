package microservice.cloud.inventory.attribute.domain.repository;

import java.util.List;

import microservice.cloud.inventory.attribute.domain.entity.AttributeDefinition;
import microservice.cloud.inventory.shared.domain.value_objects.Id;

public interface AttributeDefinitionRepository {

    public List<AttributeDefinition> getGlobalAttributes();
    public void save(AttributeDefinition attr);
    public void update(AttributeDefinition attr);
    public void delete(Id id);
}
