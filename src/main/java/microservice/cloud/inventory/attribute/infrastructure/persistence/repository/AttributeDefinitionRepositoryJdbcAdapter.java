package microservice.cloud.inventory.attribute.infrastructure.persistence.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        // TODO: pendiente
        return null;
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
    public AttributeDefinition getBySlug(Slug find_slug) {
        AttributeDefinitionEntity attrDef = attributeDefinitionJdbcRepository 
            .findBySlug(find_slug.value());

        if(attrDef == null)
            throw new DataNotFound("Attribute definition not found");

        return toMap(attrDef);
    }

    @Override
    public Map<String, AttributeDefinition> findByIds(List<String> ids) {
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

        aggregateTemplate.insert(toMap(attr));
    }

    @Transactional
    @Override
    public void update(AttributeDefinition attr) {
        aggregateTemplate.update(toMap(attr));
    }

    @Transactional
    @Override
    public void delete(AttributeDefinition attributeDefinition) {
        aggregateTemplate.delete(
            toMap(attributeDefinition)
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

    private AttributeDefinitionEntity toMap(AttributeDefinition attr) {
        return new AttributeDefinitionEntity(
            attr.id().value(),
            attr.name(),
            attr.slug().value(),
            attr.type().toString(),
            attr.is_global()
        );
    }
}
