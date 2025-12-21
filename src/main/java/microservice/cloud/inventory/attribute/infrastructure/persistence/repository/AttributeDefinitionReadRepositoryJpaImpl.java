package microservice.cloud.inventory.attribute.infrastructure.persistence.repository;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import microservice.cloud.inventory.attribute.application.ports.dto.AttributeDefinitionReadDTO;
import microservice.cloud.inventory.attribute.application.ports.out.AttributeDefinitionReadRepository;
import microservice.cloud.inventory.attribute.infrastructure.persistence.model.AttributeDefinitionEntity;
import microservice.cloud.inventory.shared.application.dto.Pagination;

@Repository
@RequiredArgsConstructor
public class AttributeDefinitionReadRepositoryJpaImpl implements AttributeDefinitionReadRepository {

    private final EntityManager entityManager;

    @Override
    public Pagination<AttributeDefinitionReadDTO> findAll(int page, int limit) {
        if (page < 1) {
            page = 1;
        }

        String jpqlData = "SELECT ca FROM AttributeDefinitionEntity ca ORDER BY ca.id ASC";

        Query dataQuery = entityManager.createQuery(jpqlData, AttributeDefinitionEntity.class);

        int firstResult = (page - 1) * limit;

        dataQuery.setFirstResult(firstResult);
        dataQuery.setMaxResults(limit);

        List<AttributeDefinitionEntity> entities = dataQuery.getResultList();

        String jpqlCount = "SELECT COUNT(ca) FROM AttributeDefinitionEntity ca";
        
        long totalItems = (Long) entityManager.createQuery(jpqlCount).getSingleResult();

        int totalPages = (int) Math.ceil((double) totalItems / limit);

        List<AttributeDefinitionReadDTO> domainObjects = entities.stream()
            .map(this::toModel)
            .collect(Collectors.toList());

        return new Pagination<AttributeDefinitionReadDTO>(domainObjects, totalPages, page);
    }

    private AttributeDefinitionReadDTO toModel(AttributeDefinitionEntity entity) {
        return new AttributeDefinitionReadDTO(
            entity.getId(),
            entity.getName(),
            entity.getSlug(),
            entity.getType()
        );
    }
}
