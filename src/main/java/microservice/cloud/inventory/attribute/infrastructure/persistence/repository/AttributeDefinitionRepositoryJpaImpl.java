package microservice.cloud.inventory.attribute.infrastructure.persistence.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import microservice.cloud.inventory.attribute.domain.entity.AttributeDefinition;
import microservice.cloud.inventory.attribute.domain.repository.AttributeDefinitionRepository;
import microservice.cloud.inventory.shared.domain.value_objects.Id;

@Repository
public class AttributeDefinitionRepositoryJpaImpl implements AttributeDefinitionRepository {

    @Override
    public List<AttributeDefinition> getGlobalAttributes() {
        return null;   
    }

    @Override
    public AttributeDefinition getById(Id id) {
        return null;
    }

    @Override
    public void save(
        AttributeDefinition attr
    ) {
        //
    }

    @Override
    public void update(AttributeDefinition attr) {
        //
    }

    @Override
    public void delete(Id id) {
        //
    }
}
