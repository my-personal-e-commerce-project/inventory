package microservice.cloud.inventory.attribute.infrastructure.persistence.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import microservice.cloud.inventory.attribute.domain.entity.AttributeDefinition;
import microservice.cloud.inventory.attribute.domain.repository.AttributeDefinitionRepository;
import microservice.cloud.inventory.attribute.domain.value_objects.DataType;
import microservice.cloud.inventory.attribute.infrastructure.persistence.model.AttributeDefinitionEntity;
import microservice.cloud.inventory.shared.domain.value_objects.Id;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;

@Repository
@RequiredArgsConstructor
public class AttributeDefinitionRepositoryJdbcAdapter implements AttributeDefinitionRepository {

    private final AttributeDefinitionJdbcRepository attributeDefinitionJdbcRepository;
    private final JdbcAggregateTemplate aggregateTemplate;

    @Override
    public List<AttributeDefinition> getGlobalAttributes() {
        return null;
    }

    @Override
    public AttributeDefinition getById(Id id) {
        return toMap(aggregateTemplate.findById(id.value(), AttributeDefinitionEntity.class));
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
    public void delete(Id id) {
        aggregateTemplate.deleteById(id.value(), AttributeDefinitionEntity.class);
    }

    private AttributeDefinition toMap(AttributeDefinitionEntity attr) {
        return new AttributeDefinition(
            new Id(attr.getId()),
            attr.getName(),
            new Slug(attr.getSlug()),
            DataType.valueOf(attr.getType()),
            attr.is_global()
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
