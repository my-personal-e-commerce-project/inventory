package microservice.cloud.inventory.attribute.infrastructure.persistence.repository;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import microservice.cloud.inventory.attribute.domain.entity.AttributeDefinition;
import microservice.cloud.inventory.attribute.domain.repository.AttributeDefinitionRepository;
import microservice.cloud.inventory.attribute.domain.value_objects.DataType;
import microservice.cloud.inventory.attribute.infrastructure.persistence.model.AttributeDefinitionEntity;
import microservice.cloud.inventory.shared.domain.exception.DataNotFound;
import microservice.cloud.inventory.shared.domain.value_objects.Id;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;

@Repository
@RequiredArgsConstructor
public class AttributeDefinitionRepositoryJdbcAdapter implements AttributeDefinitionRepository {

    private final AttributeDefinitionJdbcRepository attributeDefinitionJdbcRepository;
    private final JdbcAggregateTemplate aggregateTemplate;

    @Override
    public List<AttributeDefinition> getGlobalAttributes() {
        return attributeDefinitionJdbcRepository.findAllByIsGlobal(true)
            .stream()
            .map(this::toMap)
            .toList();
    }

    @Override
    public AttributeDefinition getById(Id id) {
        AttributeDefinitionEntity attrDef = aggregateTemplate
            .findById(id.value(), AttributeDefinitionEntity.class);

        if(attrDef == null)
            throw new DataNotFound("Attribute definition not found");

        return toMap(attrDef);
    }

    @Override
    public void isValidTheseAttributeDefinitionIds(Set<String> ids) {
        Map<String, AttributeDefinition> attrDefs = 
            this.findByIds(ids);

        ids.removeAll(attrDefs.keySet());

        if(!ids.isEmpty())
            throw new RuntimeException("These attribute definitions not exists: " + String.join(", ", ids));
    }

    @Override
    public AttributeDefinition getBySlug(Slug find_slug) {
        AttributeDefinitionEntity attrDef = attributeDefinitionJdbcRepository 
            .findBySlug(find_slug.value());

        if(attrDef == null)
            throw new DataNotFound("Attribute definition not found");

        return toMap(attrDef);
    }

    @Override
    public Map<String, AttributeDefinition> findByIds(Set<String> ids) {
        Map<String, AttributeDefinition> map = 
            new HashMap<String, AttributeDefinition>();

        attributeDefinitionJdbcRepository.findAllByIdIn(ids)
            .stream()
            .map(attr-> map.put(attr.getId(), toMap(attr)))
            .toList();

        return map;
    }

    @Transactional
    @Override
    public void save(
        AttributeDefinition attr
    ) {
        if(attributeDefinitionJdbcRepository.existsBySlug(attr.slug().value()))
            throw new RuntimeException("Slug already exists");

        aggregateTemplate.insert(factoryProductEntity(attr));
    }

    @Transactional
    @Override
    public void updateIfExists(Id id, AttributeDefinition attr) {
        AttributeDefinitionEntity attrDef = aggregateTemplate.findById(id.value(), AttributeDefinitionEntity.class);

        if(!attrDef.getSlug().equals(attr.slug().value()))
            throw new RuntimeException("Slug already exists");

        attrDef.updateFromDomain(attr);

        aggregateTemplate.update(attrDef);
    }

    @Transactional
    @Override
    public void delete(AttributeDefinition attributeDefinition) {
        aggregateTemplate.deleteById(
            attributeDefinition.id().value(),
            AttributeDefinitionEntity.class
        );
    }

    private AttributeDefinition toMap(AttributeDefinitionEntity attr) {
        return new AttributeDefinition(
            Id.fromString(attr.getId()),
            attr.getName(),
            Slug.fromString(attr.getSlug()),
            DataType.valueOf(attr.getType()),
            attr.isGlobal()
        );
    }

    private AttributeDefinitionEntity factoryProductEntity(AttributeDefinition attr) {
        return new AttributeDefinitionEntity(
            attr.id().value(),
            attr.name(),
            attr.slug().value(),
            attr.type().toString(),
            attr.is_global(),
            1L
        );
    }
}
