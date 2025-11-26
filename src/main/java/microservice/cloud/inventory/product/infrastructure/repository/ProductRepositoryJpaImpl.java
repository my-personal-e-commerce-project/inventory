package microservice.cloud.inventory.product.infrastructure.repository;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import microservice.cloud.inventory.attribute.infrastructure.entity.AttributeDefinitionEntity;
import microservice.cloud.inventory.category.infrastructure.entity.CategoryEntity;
import microservice.cloud.inventory.product.domain.entity.Product;
import microservice.cloud.inventory.product.domain.entity.ProductAttributeValue;
import microservice.cloud.inventory.product.domain.entity.ProductRepository;
import microservice.cloud.inventory.product.infrastructure.entity.ImageEntity;
import microservice.cloud.inventory.product.infrastructure.entity.ProductAttributeValueEntity;
import microservice.cloud.inventory.product.infrastructure.entity.ProductEntity;

@RequiredArgsConstructor
@Repository
public class ProductRepositoryJpaImpl implements ProductRepository {

    private final EntityManager entityManager;

    @Override
    public void save(Product product) {
        entityManager.persist(toMap(product));
    }

    private ProductEntity toMap(Product product) {

        List<ImageEntity> images = product
            .images() == null? null:
            product.images()
            .stream()
            .map((item) -> {
                return ImageEntity.builder()
                    .id(UUID.randomUUID().toString())
                    .url(item)
                    .build();
            })
            .toList();

        return ProductEntity.builder()
            .id(product.id().value())
            .title(product.title())
            .slug(product.slug().value())
            .description(product.description())
            .categories(listCategories(product))
            .price(product.price().value())
            .stock(product.stock().value())
            .images(images)
            .attributeValues(listAttributeEntities(product))
            .build();
    }

    private List<ProductAttributeValueEntity> listAttributeEntities(Product product){

        if(product.attributeValues() == null)
            return null;

        List<String> attributeDefinitionIds = product.attributeValues()
            .stream()
            .map(av -> av.attribute_definition().value())
            .toList();

        List<AttributeDefinitionEntity> definitions = entityManager.createQuery(
                "SELECT a FROM AttributeDefinitionEntity a WHERE a.id IN :ids", AttributeDefinitionEntity.class
            )
            .setParameter("ids", attributeDefinitionIds)
            .getResultList();

        if (definitions.size() != attributeDefinitionIds.size()) {
            Set<String> foundIds = definitions.stream()
                .map(AttributeDefinitionEntity::getId)
                .collect(Collectors.toSet());
            attributeDefinitionIds.removeAll(foundIds);
            throw new EntityNotFoundException("Attribute definitions not found: " + attributeDefinitionIds);
        }

        Map<String, AttributeDefinitionEntity> definitionMap = definitions.stream()
            .collect(Collectors.toMap(AttributeDefinitionEntity::getId, Function.identity()));

        List<ProductAttributeValueEntity> attributeValues = product
            .attributeValues()
            .stream()
            .map((ProductAttributeValue item) -> {
                AttributeDefinitionEntity definition = definitionMap.get(item.id().value());

                if(definition == null)
                    throw new EntityNotFoundException(
                        "Attribute definition " + item.attribute_definition().value() + " not found"
                    );

                return ProductAttributeValueEntity.builder()
                    .id(item.id().value())
                    .attribute_definition(definition)
                    .string_value(item.string_value())
                    .integer_value(item.integer_value())
                    .double_value(item.double_value())
                    .build();
            })
            .toList();

        return attributeValues;

    }

    private List<CategoryEntity> listCategories(Product product) {
        
        List<String> categoryIds = product.categories();

        List<CategoryEntity> categories = entityManager.createQuery(
                "SELECT c FROM CategoryEntity c WHERE c.id IN :ids", CategoryEntity.class
            )
            .setParameter("ids", categoryIds)
            .getResultList();

        if (categories.size() != categoryIds.size()) {
            Set<String> foundIds = categories.stream()
                .map(CategoryEntity::getId)
                .collect(Collectors.toSet());
            categoryIds.removeAll(foundIds);
            throw new EntityNotFoundException("Categories not found: " + categoryIds);
        }

        return categories;
    }
}
