package microservice.cloud.inventory.attribute.domain.repository;

import java.util.List;
import java.util.Map;

import microservice.cloud.inventory.attribute.domain.entity.AttributeDefinition;
import microservice.cloud.inventory.shared.domain.value_objects.Id;

public interface AttributeDefinitionRepository {

    public AttributeDefinition getById(Id id);
    public List<AttributeDefinition> getGlobalAttributes();
    public Map<String, AttributeDefinition> findByIds(List<String> ids);
    public void save(AttributeDefinition attr);
    public void update(AttributeDefinition attr);
    public void delete(Id id);
}
