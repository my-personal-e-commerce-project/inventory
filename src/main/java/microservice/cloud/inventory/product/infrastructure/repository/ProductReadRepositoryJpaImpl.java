package microservice.cloud.inventory.product.infrastructure.repository;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import microservice.cloud.inventory.product.application.dtos.ProductAttributeValueReadDTO;
import microservice.cloud.inventory.product.application.dtos.ProductReadDTO;
import microservice.cloud.inventory.product.application.ports.put.ProductReadRepository;
import microservice.cloud.inventory.product.infrastructure.entity.ProductEntity;
import microservice.cloud.inventory.shared.application.dto.Pagination;

@Repository
@RequiredArgsConstructor
public class ProductReadRepositoryJpaImpl implements ProductReadRepository {

    private final EntityManager entityManager;

    @Override
    public Pagination<ProductReadDTO> findAll(int page, int limit) {
        if (page < 1) {
            page = 1;
        }

        String jpqlData = "SELECT ca FROM ProductEntity ca ORDER BY ca.id ASC";

        Query dataQuery = entityManager.createQuery(jpqlData, ProductEntity.class);

        int firstResult = (page - 1) * limit;

        dataQuery.setFirstResult(firstResult);
        dataQuery.setMaxResults(limit);

        List<ProductEntity> entities = dataQuery.getResultList();

        String jpqlCount = "SELECT COUNT(ca) FROM ProductEntity ca";
        long totalItems = (Long) entityManager.createQuery(jpqlCount).getSingleResult();

        int totalPages = (int) Math.ceil((double) totalItems / limit);

        List<ProductReadDTO> domainObjects = entities.stream()
            .map(this::toModel)
            .collect(Collectors.toList());

        return new Pagination<ProductReadDTO>(domainObjects, totalPages, page);
    }

    private ProductReadDTO toModel(ProductEntity product) {
        List<ProductAttributeValueReadDTO> attrs = product.getAttributeValues().stream().map(
            attr -> {
                return ProductAttributeValueReadDTO.builder()
                    .id(attr.getId())
                    .attribute_definition_slug(attr.getAttribute_definition().getSlug())
                    .string_value(attr.getString_value())
                    .boolean_value(attr.getBoolean_value())
                    .integer_value(attr.getInteger_value())
                    .double_value(attr.getDouble_value())
                    .build();
            }
        ).toList();

        return new ProductReadDTO(
            product.getId(),
            product.getTitle(),
            product.getSlug(),
            product.getDescription(),
            product.getCategories()
                .stream()
                .map(c -> c.getId())
                .toList(),
            attrs,
            product.getPrice(),
            product.getStock(),
            product.getImages()
                .stream().map(i -> i.getUrl())
                .toList(),
            product.getTags().stream().map(t -> t.getName()).toList()
        );
    }
}
