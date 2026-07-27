package microservice.cloud.inventory.product.infrastructure.persistence.repository;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import microservice.cloud.inventory.category.infrastructure.persistence.repository.CategoryJdbcRepository;
import microservice.cloud.inventory.product.domain.entity.Product;
import microservice.cloud.inventory.product.domain.entity.ProductAttributeValue;
import microservice.cloud.inventory.product.domain.entity.ProductRepository;
import microservice.cloud.inventory.product.domain.value_objects.Price;
import microservice.cloud.inventory.product.domain.value_objects.Quantity;
import microservice.cloud.inventory.product.infrastructure.persistence.entity.ProductAttributeValueEntity;
import static microservice.cloud.inventory.product.infrastructure.persistence.entity.ProductEntity.ProductCategoryReference;
import microservice.cloud.inventory.product.infrastructure.persistence.entity.ProductEntity;
import microservice.cloud.inventory.productStock.infrastructure.persistation.repository.ProductStockEntity;
import microservice.cloud.inventory.shared.domain.exception.DataNotFound;
import microservice.cloud.inventory.shared.domain.value_objects.Id;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryJdbcAdapter implements ProductRepository {

    private final JdbcAggregateTemplate aggregateTemplate;
    private final ProductAttributeValueJdbcRepository productAttributeValueJdbcRepository;
    private final ProductJdbcRepository productJdbcRepository;
    private final CategoryJdbcRepository categoryJdbcRepository;

    @Override
    public ProductAttributeValue findProductAttributeValueById(Id id) {
        ProductAttributeValueEntity attr = productAttributeValueJdbcRepository
            .findByAttributeDefinitionId(id.value());

        if(attr == null)
            throw new RuntimeException("Product attribute value not found");

        return toMap(
            attr
        );
    }

    @Override
    public Product findBySlug(Slug slug) {
        ProductEntity entity = productJdbcRepository
            .findBySlug(slug.value())
            .orElseThrow(() -> new DataNotFound("Product not found"));

        return toMap(entity);
    }

    @Transactional
    @Override
    public void createProductAndStock(Product product, Id productStockId, Quantity stock) {
        if(productJdbcRepository.existsBySlug(product.slug().value()))
            throw new RuntimeException("This slug already exists");

        if(product.categories() != null && !product.categories().isEmpty() && categoryJdbcRepository.countByIdIn(product.categories()) == 0)
            throw new RuntimeException("Not all provided category ids are valid");

        try {
            aggregateTemplate.insert(factoryProductEntity(product));
            aggregateTemplate.insert(new ProductStockEntity(productStockId.value(), product.id().value(), stock.value(), 1L));
        } catch(DataIntegrityViolationException e) {
            if (e.getMessage() != null && e.getMessage().contains("slug")) {
                throw new RuntimeException("The slug already exists");
            }
            throw e;
        }
    }

    @Transactional
    @Override
    public Product updateIfExists(Id id, Consumer<Product> function) {
        ProductEntity entity = productJdbcRepository
            .findById(id.value())
            .orElseThrow(() -> new DataNotFound("Product not found"));

        Product product = toMap(entity);

        if(
            product.categories() != null
            && !product.categories().isEmpty()
            && categoryJdbcRepository.countByIdIn(product.categories()) != product.categories().size()
        )
            throw new RuntimeException("Not all provided category ids are valid");

        if(!entity.getSlug().equals(product.slug().value()) && productJdbcRepository.existsBySlug(product.slug().value()))
            throw new RuntimeException("The slug already exists");

        function.accept(product);

        entity.updateFromDomain(product);

        try {
            aggregateTemplate.update(entity);
        } catch (Throwable t) { 
            Throwable root = t;
            while (root != null) {
                String msg = root.getMessage();
                if (msg != null) {
                    if (msg.contains("fk_pav_definition")) {
                        throw new RuntimeException("The attribute definition not found");
                    }

                    if (msg.contains("products_slug_key")) {
                        throw new RuntimeException("The slug already exists");
                    }
                }
                root = root.getCause();
            }
            throw t; 
        }
        return product;
    }

    @Override
    public void delete(Product product) {
        aggregateTemplate.deleteById(product.id().value(), ProductEntity.class);
    }

    private ProductEntity factoryProductEntity(Product product) {

        Set<ProductCategoryReference> categories = product.categories() == null
            ? null
            : product.categories()
                .stream()
                .map(cat -> new ProductCategoryReference(cat))
                .collect(Collectors.toSet());

        return new ProductEntity(
            product.id().value(),
            product.title(),
            product.slug().value(),
            product.description(),
            categories,
            product.isActive(),
            product.price().value(),
            product.minStock().value(),
            product.images() == null? null: new HashSet<>(product.images()),
            product.attributeValues()
                .stream()
                .map(attr -> new ProductAttributeValueEntity(
                        attr.id().value(),
                        product.id().value(),
                        attr.attribute_definition_id().value(),
                        attr.string_value(),
                        attr.integer_value(),
                        attr.double_value(),
                        attr.boolean_value()
                    )
                ).collect(Collectors.toSet()),
            product.tags(),
            1L
        );
    }

    private Product toMap(ProductEntity product) {
        Set<String> categories = product.getCategories()
                .stream()
                .map(cat -> cat.categoryId())
                .collect(Collectors.toSet());

        return new Product(
            Id.fromString(product.getId()),
            product.getTitle(),
            Slug.fromString(product.getSlug()),
            product.getDescription(),
            categories,
            product.isActive(),
            new Price(product.getPrice()),
            product.getAttributeValues()
                .stream()
                .map(attr -> new ProductAttributeValue(
                        Id.fromString(attr.getId()),
                        Id.fromString(
                            attr.getAttribute_definition_id()
                        ),
                        attr.getString_value(),
                        attr.getInteger_value(),
                        attr.getDouble_value(),
                        attr.getBoolean_value()
                    )
                ).collect(Collectors.toSet()),
            product.getMinStock() == null? null: new Quantity(product.getMinStock()),
            product.getImages(),
            product.getTags()
        );
    }

    private ProductAttributeValue toMap(ProductAttributeValueEntity entity) {
        return new ProductAttributeValue(
            Id.fromString(entity.getId()),
            Id.fromString(entity.getAttribute_definition_id()),
            entity.getString_value(),
            entity.getInteger_value(),
            entity.getDouble_value(),
            entity.getBoolean_value()
        );
    }
}
