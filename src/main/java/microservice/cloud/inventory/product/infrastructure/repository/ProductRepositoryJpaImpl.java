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
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import microservice.cloud.inventory.attribute.infrastructure.persistence.model.AttributeDefinitionEntity;
import microservice.cloud.inventory.category.infrastructure.entity.CategoryEntity;
import microservice.cloud.inventory.product.domain.entity.Product;
import microservice.cloud.inventory.product.domain.entity.ProductAttributeValue;
import microservice.cloud.inventory.product.domain.entity.ProductRepository;
import microservice.cloud.inventory.product.domain.value_objects.Price;
import microservice.cloud.inventory.product.domain.value_objects.Quantity;
import microservice.cloud.inventory.product.infrastructure.entity.ImageEntity;
import microservice.cloud.inventory.product.infrastructure.entity.ProductAttributeValueEntity;
import microservice.cloud.inventory.product.infrastructure.entity.ProductEntity;
import microservice.cloud.inventory.product.infrastructure.entity.TagEntity;
import microservice.cloud.inventory.shared.domain.exception.DataNotFound;
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
            throw new DataNotFound("Product not found");

        return toModel(product);
    }

    @Transactional
    @Override
    public void update(Product product) {
        ProductEntity entity = entityManager
            .find(ProductEntity.class, product.id().value());

        if (entity == null)
            throw new DataNotFound("Product not found");

        entity.getTags().clear();

        List<TagEntity> tags = product.images() == null
            ? Collections.emptyList()
            : product.tags()
            .stream()
            .map((item) -> {
                return TagEntity.builder()
                    .id(UUID.randomUUID().toString())
                    .product(entity)
                    .name(item)
                    .build();
            })
        .toList();

        entity.getTags().addAll(tags);

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

        entityManager.merge(entity);
    }

    @Override
    @Transactional
    public void save(Product product) {
        if(existBySlug(product.slug().value())) {
            throw new RuntimeException("Product with slug " + product.slug().value() + " already exists");
        }
        entityManager.persist(toMap(product));
    }

    @Transactional
    @Override
    public void delete(Product product) {
        ProductEntity productDB = entityManager
            .find(ProductEntity.class,
                product.id().value()
            );

        if(productDB == null)
            throw new DataNotFound("Product not found");

        entityManager.remove(productDB);
    }

    private Product toModel(ProductEntity entity) {
        List<ProductAttributeValue> attrs = entity.getAttributeValues().stream().map(a -> {
            return new ProductAttributeValue(
                    new Id(a.getId()), 
                    new Slug(a.getAttribute_definition().getSlug()), 
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
                entity.getImages().stream().map(i -> i.getUrl()).toList(),
                entity.getTags().stream().map(t -> t.getName()).toList()
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

        List<TagEntity> tags = product
            .tags() == null? null:
            product.tags()
            .stream()
            .map(i -> TagEntity.builder()
                    .id(UUID.randomUUID().toString()).name(i)
                    .product(p)
                    .build()
                )
            .toList();

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
        p.setTags(tags);
        p.setAttributeValues(listAttributeEntities(product, p));

        return p;
    }

    private List<ProductAttributeValueEntity> listAttributeEntities(Product product, ProductEntity entity){

        if(product.attributeValues() == null)
            return null;

        List<String> attributeDefinitionIds = product.attributeValues()
            .stream()
            .map(av -> av.attribute_definition_slug().value())
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
                AttributeDefinitionEntity definition = definitionMap.get(item.attribute_definition_slug().value());

                if(definition == null)
                    throw new EntityNotFoundException(
                            "Attribute definition " + item.attribute_definition_slug().value() + " not found"
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

    private boolean existBySlug(String slug) {
        String jpql = "SELECT COUNT(c) FROM ProductEntity c WHERE c.slug = :slug";

        try {
            Long count = entityManager.createQuery(jpql, Long.class)
                .setParameter("slug", slug)
                .getSingleResult(); 
            
            return count > 0;
        } catch (NoResultException e) {
            return false;
        }
    }
}
