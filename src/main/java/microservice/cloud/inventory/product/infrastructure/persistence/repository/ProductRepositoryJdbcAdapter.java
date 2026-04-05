package microservice.cloud.inventory.product.infrastructure.persistence.repository;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import microservice.cloud.inventory.attribute.domain.entity.AttributeDefinition;
import microservice.cloud.inventory.attribute.domain.value_objects.DataType;
import microservice.cloud.inventory.product.domain.entity.Product;
import microservice.cloud.inventory.product.domain.entity.ProductAttributeValue;
import microservice.cloud.inventory.product.domain.entity.ProductRepository;
import microservice.cloud.inventory.product.domain.value_objects.Price;
import microservice.cloud.inventory.product.domain.value_objects.Quantity;
import microservice.cloud.inventory.product.infrastructure.persistence.entity.ProductAttributeValueEntity;
import static microservice.cloud.inventory.product.infrastructure.persistence.entity.ProductEntity.ProductCategoryReference;
import microservice.cloud.inventory.product.infrastructure.persistence.entity.ProductEntity;
import microservice.cloud.inventory.shared.domain.exception.DataNotFound;
import microservice.cloud.inventory.shared.domain.value_objects.Id;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryJdbcAdapter implements ProductRepository {

    private final JdbcAggregateTemplate aggregateTemplate;
    private final JdbcTemplate jdbcTemplate;
    private final ProductAttributeValueJdbcRepository productAttributeValueJdbcRepository;
    private final ProductJdbcRepository productJdbcRepository;

    @Override
    public void getProductIdsWithTheseCategoriesAndPriceRange(Price minPrice, Price maxPrice, Set<String> categoryIds) {
        // TODO Auto-generated method stub
    }

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

    @Transactional
    @Override
    public void massCreateDefaultProductAttributeValues(AttributeDefinition attributeDefinition) {
        String sql = """
            INSERT INTO product_attribute_values (
                id, attribute_definition_id, product_id, 
                string_value, integer_value, double_value, boolean_value
            )
            SELECT 
                gen_random_uuid()::text, ?, id, 
                ?, ?, ?, ?
            FROM products
            ON CONFLICT (product_id, attribute_id) 
            DO NOTHING;
            """;

        String defId = attributeDefinition.id().value();

        switch(attributeDefinition.type().toString()){
            case "STRING" -> 
                jdbcTemplate.update(sql, defId, "", null, null, null);
            case "INTEGER" -> 
                jdbcTemplate.update(sql, defId, null, 0, null, null);
            case "DOUBLE" -> 
                jdbcTemplate.update(sql, defId, null, null, 0.0, null);
            case "BOOLEAN" -> 
                jdbcTemplate.update(sql, defId, null, null, null, false);
        }
    }

    @Transactional
    @Override
    public void massCreateProductAttributeValuesByCategory(Id categoryId, AttributeDefinition attributeDefinition) {
        String sql = """
            INSERT INTO product_attribute_values (product_id, attribute_id, value)
                SELECT gen_random_uuid()::text, pc.product_id, ?, ?, ?, ?, ? 
                FROM product_categories pc
                WHERE pc.category_id = ?
                ON CONFLICT (product_id, attribute_id) 
                DO NOTHING;
            """;

        String defId = attributeDefinition.id().value();

        switch(attributeDefinition.type().toString()){
            case "STRING" -> 
                jdbcTemplate.update(sql, defId, "", null, null, null, categoryId.value());
            case "INTEGER" -> 
                jdbcTemplate.update(sql, defId, null, 0, null, null, categoryId.value());
            case "DOUBLE" -> 
                jdbcTemplate.update(sql, defId, null, null, 0.0, null, categoryId.value());
            case "BOOLEAN" -> 
                jdbcTemplate.update(sql, defId, null, null, null, false, categoryId.value());
        }
    }

    @Transactional
    @Override
    public void updateTheValueTypeOfProductAttributesByAttributeDefinition(Id attributeDefinitionId, DataType type) {
        String sql = """
            UPDATE product_attribute_values 
            SET string_value = ?, 
                integer_value = ?, 
                double_value = ?, 
                boolean_value = ?
            WHERE attribute_definition_id = ?;
            """;

        String defId = attributeDefinitionId.value();

        switch(type.toString()) {
            case "STRING" -> 
                jdbcTemplate.update(sql, "", null, null, null, defId);
            case "INTEGER" -> 
                jdbcTemplate.update(sql, null, 0, null, null, defId);
            case "DOUBLE" -> 
                jdbcTemplate.update(sql, null, null, 0.0, null, defId);
            case "BOOLEAN" -> 
                jdbcTemplate.update(sql, null, null, null, false, defId);
        }
    }

    @Override
    public void deleteOrphanAttributeValues(Id categoryId, Id attributeDefinitionId) {
        productJdbcRepository
            .deleteOrphanAttributeValues(attributeDefinitionId.value(), categoryId.value());
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
    public void save(Product product) {
        try {
            aggregateTemplate.insert(toMap(product));
        } catch(DataIntegrityViolationException e) {
            if (e.getMessage() != null && e.getMessage().contains("slug")) {
                throw new RuntimeException("The slug already exists");
            }
            throw e;
        }
    }

    @Transactional
    @Override
    public void update(Product product) {
        try {
            aggregateTemplate.update(toMap(product));
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
    }

    @Override
    public void delete(Product product) {
        aggregateTemplate.delete(toMap(product));
    }

    private ProductEntity toMap(Product product) {

        Set<ProductCategoryReference> categories = product.categories()
                .stream()
                .map(cat -> new ProductCategoryReference(cat))
                .collect(Collectors.toSet());

        return new ProductEntity(
            product.id().value(),
            product.title(),
            product.slug().value(),
            product.description(),
            categories,
            product.price().value(),
            product.stock().value(),
            new HashSet<>(product.images()),
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
            product.tags()
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
            null,
            new Quantity(product.getStock()),
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
