package microservice.cloud.inventory.product.infrastructure.persistence.repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import microservice.cloud.inventory.attribute.domain.value_objects.DataType;
import microservice.cloud.inventory.attribute.infrastructure.persistence.repository.AttributeDefinitionJdbcRepository;
import microservice.cloud.inventory.category.infrastructure.persistence.repository.CategoryJdbcRepository;
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
    private final AttributeDefinitionJdbcRepository attributeDefinitionJdbcRepository;
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

    @Transactional
    @Override
    public void massCreateDefaultProductAttributeValues(Id id, DataType type) {
        String defId = id.value();

        switch(type.toString()){
            case "STRING" -> 
                productJdbcRepository.massCreateDefaultProductAttributeValues(defId, "", null, null, null);
            case "INTEGER" -> 
                productJdbcRepository.massCreateDefaultProductAttributeValues(defId, null, 0, null, null);
            case "DOUBLE" -> 
                productJdbcRepository.massCreateDefaultProductAttributeValues(defId, null, null, 0.0, null);
            case "BOOLEAN" -> 
                productJdbcRepository.massCreateDefaultProductAttributeValues(defId, null, null, null, false);
        }
    }

    @Transactional
    @Override
    public void massCreateProductAttributeValuesByNewRequiredCategoryAttributes(Id categoryId, List<Id> attributeDefinitionIds) {
        String sql = """
            INSERT INTO product_attribute_values (
                id, product_id, attribute_definition_id, 
                string_value, integer_value, double_value, boolean_value
            )
            SELECT 
                gen_random_uuid()::text, pc.product_id, ?, ?, ?, ?, ? 
            FROM product_categories pc
            WHERE pc.category_id = ?
            ON CONFLICT (product_id, attribute_definition_id) 
            DO NOTHING;
            """;

        var definitions = attributeDefinitionJdbcRepository.findAllByIdIn(
            attributeDefinitionIds.stream().map(Id::value).toList()
        );

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                var ad = definitions.get(i);
                ps.setString(1, ad.getId());
                
                ps.setString(2, "STRING".equals(ad.getType()) ? "" : null);
                ps.setObject(3, "INTEGER".equals(ad.getType()) ? 0 : null, java.sql.Types.INTEGER);
                ps.setObject(4, "DOUBLE".equals(ad.getType()) ? 0.0 : null, java.sql.Types.DOUBLE);
                ps.setObject(5, "BOOLEAN".equals(ad.getType()) ? false : null, java.sql.Types.BOOLEAN);
                
                ps.setString(6, categoryId.value());
            }

            @Override
            public int getBatchSize() {
                return definitions.size();
            }
        });
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
    public Product findBySlug(Slug slug) {
        ProductEntity entity = productJdbcRepository
            .findBySlug(slug.value())
            .orElseThrow(() -> new DataNotFound("Product not found"));

        return toMap(entity);
    }

    @Transactional
    @Override
    public void save(Product product) {
        if(productJdbcRepository.existsBySlug(product.slug().value()))
            throw new RuntimeException("This slug already exists");

        if(product.categories() != null && categoryJdbcRepository.countByIdIn(product.categories()) == 0)
            throw new RuntimeException("Not all provided category ids are valid");

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
        if(productJdbcRepository.existsBySlug(product.slug().value()))
            throw new RuntimeException("This slug already exists");

        if(
            product.categories() != null 
            && categoryJdbcRepository.countByIdIn(product.categories()) != product.categories().size()
        )
            throw new RuntimeException("Not all provided category ids are valid");

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
            product.price().value(),
            product.stock().value(),
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
