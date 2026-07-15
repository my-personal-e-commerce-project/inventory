package microservice.cloud.inventory.product.infrastructure.persistence.repository;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.jdbc.core.JdbcTemplate;

import microservice.cloud.inventory.attribute.infrastructure.persistence.repository.AttributeDefinitionJdbcRepository;
import microservice.cloud.inventory.category.infrastructure.persistence.repository.CategoryJdbcRepository;
import microservice.cloud.inventory.product.domain.entity.Product;
import microservice.cloud.inventory.product.domain.entity.ProductAttributeValue;
import microservice.cloud.inventory.product.domain.value_objects.Price;
import microservice.cloud.inventory.product.domain.value_objects.Quantity;
import microservice.cloud.inventory.product.infrastructure.persistence.entity.ProductAttributeValueEntity;
import microservice.cloud.inventory.product.infrastructure.persistence.entity.ProductEntity;
import microservice.cloud.inventory.product.infrastructure.persistence.entity.ProductEntity.ProductCategoryReference;
import microservice.cloud.inventory.shared.domain.exception.DataNotFound;
import microservice.cloud.inventory.shared.domain.value_objects.Id;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductRepositoryJdbcAdapterTest {

    @Mock
    private JdbcAggregateTemplate aggregateTemplate;
    @Mock
    private JdbcTemplate jdbcTemplate;
    @Mock
    private ProductAttributeValueJdbcRepository productAttributeValueJdbcRepository;
    @Mock
    private AttributeDefinitionJdbcRepository attributeDefinitionJdbcRepository;
    @Mock
    private ProductJdbcRepository productJdbcRepository;
    @Mock
    private CategoryJdbcRepository categoryJdbcRepository;

    @InjectMocks
    private ProductRepositoryJdbcAdapter repositoryAdapter;

    private Product createDomainProduct() {
        return new Product(
            Id.fromString("prod-123"),
            "Sample Product",
            Slug.fromString("sample-product"),
            "Description",
            Set.of("cat-1"),
            true,
            new Price(100.0),
            new HashSet<>(),
            new Quantity(10),
            null,
            Set.of("image1.png"),
            Set.of("tag1")
        );
    }

    private ProductEntity createProductEntity() {
        return new ProductEntity(
            "prod-123",
            "Sample Product",
            "sample-product",
            "Description",
            Set.of(new ProductCategoryReference("cat-1")),
            true,
            100.0,
            10,
            5,
            Set.of("image1.png"),
            new HashSet<>(),
            Set.of("tag1")
        );
    }

    @Test
    void shouldFindProductAttributeValueByIdSuccessfully() {
        // GIVEN
        Id id = Id.fromString("attr-123");
        ProductAttributeValueEntity entity = new ProductAttributeValueEntity(
            "attr-123", "prod-123", "def-123", "string-val", null, null, null
        );
        when(productAttributeValueJdbcRepository.findByAttributeDefinitionId(id.value()))
            .thenReturn(entity);

        // WHEN
        ProductAttributeValue result = repositoryAdapter.findProductAttributeValueById(id);

        // THEN
        assertNotNull(result);
        assertEquals("attr-123", result.id().value());
        assertEquals("def-123", result.attribute_definition_id().value());
        assertEquals("string-val", result.string_value());
    }

    @Test
    void shouldThrowExceptionWhenProductAttributeValueNotFound() {
        // GIVEN
        Id id = Id.fromString("attr-123");
        when(productAttributeValueJdbcRepository.findByAttributeDefinitionId(id.value()))
            .thenReturn(null);

        // WHEN & THEN
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> repositoryAdapter.findProductAttributeValueById(id)
        );
        assertEquals("Product attribute value not found", exception.getMessage());
    }

    @Test
    void shouldFindBySlugSuccessfully() {
        // GIVEN
        Slug slug = Slug.fromString("sample-product");
        ProductEntity entity = createProductEntity();
        when(productJdbcRepository.findBySlug(slug.value())).thenReturn(Optional.of(entity));

        // WHEN
        Product product = repositoryAdapter.findBySlug(slug);

        // THEN
        assertNotNull(product);
        assertEquals("prod-123", product.id().value());
        assertEquals("sample-product", product.slug().value());
    }

    @Test
    void shouldThrowDataNotFoundWhenSlugDoesNotExist() {
        // GIVEN
        Slug slug = Slug.fromString("non-existent");
        when(productJdbcRepository.findBySlug(slug.value())).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(
            DataNotFound.class,
            () -> repositoryAdapter.findBySlug(slug)
        );
    }

    @Test
    void shouldSaveProductSuccessfully() {
        // GIVEN
        Product product = createDomainProduct();
        when(productJdbcRepository.existsBySlug(product.slug().value())).thenReturn(false);
        when(categoryJdbcRepository.countByIdIn(product.categories())).thenReturn(1L);

        // WHEN
        assertDoesNotThrow(() -> repositoryAdapter.save(product));

        // THEN
        verify(aggregateTemplate, times(1)).insert(any(ProductEntity.class));
    }

    @Test
    void shouldThrowExceptionOnSaveWhenSlugExists() {
        // GIVEN
        Product product = createDomainProduct();
        when(productJdbcRepository.existsBySlug(product.slug().value())).thenReturn(true);

        // WHEN & THEN
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> repositoryAdapter.save(product)
        );
        assertEquals("This slug already exists", exception.getMessage());
        verifyNoInteractions(categoryJdbcRepository, aggregateTemplate);
    }

    @Test
    void shouldThrowExceptionOnSaveWhenCategoriesAreInvalid() {
        // GIVEN
        Product product = createDomainProduct();
        when(productJdbcRepository.existsBySlug(product.slug().value())).thenReturn(false);
        // Retorna 0 indicando que las categorías no existen en base de datos
        when(categoryJdbcRepository.countByIdIn(product.categories())).thenReturn(0L);

        // WHEN & THEN
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> repositoryAdapter.save(product)
        );
        assertEquals("Not all provided category ids are valid", exception.getMessage());
        verify(aggregateTemplate, never()).insert(any(ProductEntity.class));
    }

    @Test
    void shouldUpdateProductSuccessfully() {
        // GIVEN
        Product product = createDomainProduct();
        when(productJdbcRepository.existsBySlug(product.slug().value())).thenReturn(false);
        when(categoryJdbcRepository.countByIdIn(product.categories())).thenReturn((long) product.categories().size());

        // WHEN
        assertDoesNotThrow(() -> repositoryAdapter.update(product));

        // THEN
        verify(aggregateTemplate, times(1)).update(any(ProductEntity.class));
    }

    @Test
    void shouldDeleteProductSuccessfully() {
        // GIVEN
        Product product = createDomainProduct();

        // WHEN
        assertDoesNotThrow(() -> repositoryAdapter.delete(product));

        // THEN
        verify(aggregateTemplate, times(1)).delete(any(ProductEntity.class));
    }
}
