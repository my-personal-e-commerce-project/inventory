package microservice.cloud.inventory.attribute.infrastructure.persistence.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import microservice.cloud.inventory.attribute.domain.entity.AttributeDefinition;
import microservice.cloud.inventory.attribute.domain.repository.AttributeDefinitionRepository;
import microservice.cloud.inventory.attribute.domain.value_objects.DataType;
import microservice.cloud.inventory.attribute.infrastructure.persistence.model.AttributeDefinitionEntity;
import microservice.cloud.inventory.shared.domain.value_objects.Id;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;

@Repository
@RequiredArgsConstructor
public class AttributeDefinitionRepositoryJpaImpl implements AttributeDefinitionRepository {

    private final EntityManager entityManager;

    @Override
    public List<AttributeDefinition> getGlobalAttributes() {
        String hql = "FROM AttributeDefinitionEntity a WHERE is_global = true";

        List<AttributeDefinitionEntity> result = entityManager.createQuery(hql, AttributeDefinitionEntity.class)
            .getResultList();

        return result.stream().map(a -> toMap(a)).toList();
    }

    @Override
    public AttributeDefinition getById(Id id) {
        AttributeDefinitionEntity entity = entityManager
            .find(AttributeDefinitionEntity.class, id.value());

        return toMap(entity);
    }

    @Override
    @Transactional
    public void save(
        AttributeDefinition attr
    ) {
        entityManager.persist(toMap(attr));
    }

    @Override
    public void update(AttributeDefinition attr) {
        entityManager.persist(toMap(attr));
    }

    @Override
    public void delete(Id id) {
        AttributeDefinitionEntity attr = 
            entityManager.find(AttributeDefinitionEntity.class, id.value());

        if(attr == null)
            throw new EntityNotFoundException("Attribute definition not found");

        entityManager.remove(attr);
    }

    private AttributeDefinitionEntity toMap(AttributeDefinition attr) {

        return new AttributeDefinitionEntity(
                attr.id().value(),
                attr.name(),
                attr.slug().value(),
                attr.type().toString(),
                attr.is_global(),
                null
        );
    }

    private AttributeDefinition toMap(AttributeDefinitionEntity attributeDefinitionEntity) {

        return new AttributeDefinition(
            new Id(attributeDefinitionEntity.getId()), 
            attributeDefinitionEntity.getName(),
            new Slug(attributeDefinitionEntity.getSlug()),
            DataType.valueOf(attributeDefinitionEntity.getType()),
           attributeDefinitionEntity.is_global()
        );
    }
}
