package microservice.cloud.inventory.product.infrastructure.repository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import microservice.cloud.inventory.attribute.infrastructure.entity.AttributeDefinitionEntity;
import microservice.cloud.inventory.category.infrastructure.entity.CategoryEntity;
import microservice.cloud.inventory.product.domain.entity.Product;
import microservice.cloud.inventory.product.domain.entity.ProductAttributeValue;
import microservice.cloud.inventory.product.domain.entity.ProductRepository;
import microservice.cloud.inventory.product.domain.value_objects.Price;
import microservice.cloud.inventory.product.domain.value_objects.Quantity;
import microservice.cloud.inventory.product.infrastructure.entity.ImageEntity;
import microservice.cloud.inventory.product.infrastructure.entity.ProductAttributeValueEntity;
import microservice.cloud.inventory.product.infrastructure.entity.ProductEntity;
import microservice.cloud.inventory.shared.domain.value_objects.Id;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;

@RequiredArgsConstructor
@Repository
public class ProductRepositoryJpaImpl implements ProductRepository {

    private final EntityManager entityManager;

    @Override
    public Product findById(Id id) {
        ProductEntity product = entityManager
            .find(ProductEntity.class, id.value());

        if(product == null)
            throw new EntityNotFoundException("Product not found");

        return toModel(product);
    }

    @Transactional
    @Override
    public void update(Product product) {
        ProductEntity entity = entityManager
            .find(ProductEntity.class, product.id().value());

        if (entity == null)
            throw new EntityNotFoundException("Product not found");

        entity.getImages().clear();

        List<ImageEntity> images = product.images() == null
            ? Collections.emptyList()
            : product.images()
            .stream()
            .map((item) -> {
                return ImageEntity.builder()
                    .id(UUID.randomUUID().toString())
                    .product(entity)
                    .url(item)
                    .build();
            })
        .toList();

        entity.getImages().addAll(images);

        entity.setTitle(product.title());
        entity.setDescription(product.description());
        entity.setSlug(product.slug().value());
        entity.setStock(product.stock().value());
        entity.setPrice(product.price().value());
        entity.setCategories(listCategories(product));

        entity.getAttributeValues().clear();

        entityManager.flush();

        entity.getAttributeValues().addAll(listAttributeEntities(product, entity));
    }

    @Override
    @Transactional
    public void addProductAttribute(Id productId, ProductAttributeValue attr) {
        ProductEntity product = entityManager.find(
                ProductEntity.class, 
                productId.value()
                );

        AttributeDefinitionEntity definition = entityManager.find(
                AttributeDefinitionEntity.class, 
                attr.attribute_definition().value()
                );

        if(product == null)
            throw new EntityNotFoundException("Product not found");

        if(definition == null)
            throw new EntityNotFoundException("Attribute definition not found");

        product.getAttributeValues().add(
                ProductAttributeValueEntity.builder()
                .id(attr.id().value())
                .product(product)
                .attribute_definition(definition)
                .string_value(attr.string_value())
                .integer_value(attr.integer_value())
                .double_value(attr.double_value())
                .build()
            );

        entityManager.merge(product);
    }

    @Override
    @Transactional
    public void save(Product product) {
        entityManager.persist(toMap(product));
    }

    @Transactional
    @Override
    public void delete(Id id) {
        ProductEntity product = entityManager.find(ProductEntity.class, id.value());

        if(product == null)
            throw new EntityNotFoundException("Product not found");
        entityManager.remove(product);
    }

    @Transactional
    @Override
    public void removeProductAttribute(Id productId, Id productAttributeId) {
        ProductEntity product = entityManager.find(ProductEntity.class, productId.value());

        ProductAttributeValueEntity toRemove = product.getAttributeValues()
            .stream()
            .filter(attr -> attr.getId().equals(productAttributeId.value()))
            .findFirst()
            .orElseThrow(() -> new EntityNotFoundException("Product attribute not found"));

        product.getAttributeValues().remove(toRemove);

        entityManager.flush();
    }

    private Product toModel(ProductEntity entity) {
        List<ProductAttributeValue> attrs = entity.getAttributeValues().stream().map(a -> {
            return new ProductAttributeValue(
                    new Id(a.getId()), 
                    new Id(a.getAttribute_definition().getId()), 
                    a.getString_value(), 
                    a.getInteger_value(), 
                    a.getDouble_value(), 
                    a.getBoolean_value()
                    );
        }).toList();

        return new Product(
                new Id(entity.getId()),
                entity.getTitle(), 
                new Slug(entity.getSlug()), 
                entity.getDescription(), 
                entity.getCategories().stream().map(c -> c.getId()).toList(), 
                new Price(entity.getPrice()), 
                attrs, 
                new Quantity(entity.getStock()), 
                entity.getImages().stream().map(i -> i.getUrl()).toList()
                );
    }

    private ProductEntity toMap(Product product) {
        ProductEntity p = ProductEntity.builder()
            .id(product.id().value())
            .title(product.title())
            .slug(product.slug().value())
            .description(product.description())
            .categories(listCategories(product))
            .price(product.price().value())
            .stock(product.stock().value())
            .build();

        List<ImageEntity> images = product
            .images() == null? null:
            product.images()
            .stream()
            .map((item) -> {
                return ImageEntity.builder()
                    .id(UUID.randomUUID().toString())
                    .url(item)
                    .product(p)
                    .build();
            })
        .toList();

        p.setImages(images);
        p.setAttributeValues(listAttributeEntities(product, p));

        return p;
    }

    private List<ProductAttributeValueEntity> listAttributeEntities(Product product, ProductEntity entity){

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

        Map<String, AttributeDefinitionEntity> definitionMap = definitions.stream()
            .collect(Collectors.toMap(AttributeDefinitionEntity::getId, Function.identity()));

        List<ProductAttributeValueEntity> attributeValues = product
            .attributeValues()
            .stream()
            .map((ProductAttributeValue item) -> {
                AttributeDefinitionEntity definition = definitionMap.get(item.attribute_definition().value());

                if(definition == null)
                    throw new EntityNotFoundException(
                            "Attribute definition " + item.attribute_definition().value() + " not found"
                            );

                return ProductAttributeValueEntity.builder()
                    .id(item.id().value())
                    .product(entity)
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
