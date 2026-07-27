package microservice.cloud.inventory.attribute.domain.repository;

import java.util.List;
import java.util.Map;
import java.util.Set;

import microservice.cloud.inventory.attribute.domain.entity.AttributeDefinition;
import microservice.cloud.inventory.shared.domain.value_objects.Id;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;

public interface AttributeDefinitionRepository {

    public AttributeDefinition getById(Id id);
    public AttributeDefinition getBySlug(Slug find_slug);
    public List<AttributeDefinition> getGlobalAttributes();
    public Map<String, AttributeDefinition> findByIds(Set<String> ids);
    public void isValidTheseAttributeDefinitionIds(Set<String> ids);
    public void save(AttributeDefinition attr);
    public void updateIfExists(Id id, AttributeDefinition attr);
    public void delete(AttributeDefinition attrDef);
}
