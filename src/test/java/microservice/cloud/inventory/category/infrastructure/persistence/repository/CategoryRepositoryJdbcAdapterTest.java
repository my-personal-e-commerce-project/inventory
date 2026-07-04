package microservice.cloud.inventory.category.infrastructure.persistence.repository;

import java.util.HashSet;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.jdbc.core.JdbcTemplate;

import microservice.cloud.inventory.attribute.infrastructure.persistence.repository.AttributeDefinitionJdbcRepository;
import microservice.cloud.inventory.category.domain.entity.Category;
import microservice.cloud.inventory.category.domain.entity.CategoryAttribute;
import microservice.cloud.inventory.category.domain.value_objects.Status;
import microservice.cloud.inventory.category.infrastructure.persistence.model.CategoryAttributeEntity;
import microservice.cloud.inventory.category.infrastructure.persistence.model.CategoryEntity;
import microservice.cloud.inventory.shared.domain.exception.DataNotFound;
import microservice.cloud.inventory.shared.domain.value_objects.Id;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryRepositoryJdbcAdapterTest {

    @Mock
    private AttributeDefinitionJdbcRepository attributeDefinitionJdbcRepository;
    @Mock
    private CategoryJdbcRepository categoryJdbcRepository;
    @Mock
    private JdbcAggregateTemplate aggregateTemplate;
    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private CategoryRepositoryJdbcAdapter repositoryAdapter;

    private Category createDomainCategory() {
        return new Category(
            Id.fromString("cat-123"),
            "Electronics",
            Slug.fromString("electronics"),
            null,
            Status.ENABLED,
            new HashSet<>()
        );
    }

    private CategoryEntity createCategoryEntity() {
        return new CategoryEntity(
            "cat-123",
            "Electronics",
            "electronics",
            null,
            Status.ENABLED.name(),
            new HashSet<>()
        );
    }

    @Test
    void shouldGetCategoryAttributeByAttributeDefinitionIdSuccessfully() {
        // GIVEN
        Id id = Id.fromString("def-123");
        CategoryAttributeEntity entity = new CategoryAttributeEntity(
            "attr-123", "cat-123", "def-123", null, true, true, true
        );

        when(jdbcTemplate.query(anyString(), any(CategoryAttributeResultSetExtractor.class), eq("def-123")))
            .thenReturn(entity);

        // WHEN
        CategoryAttribute result = repositoryAdapter.getCategoryAttributeByAttributeDefinitionId(id);

        // THEN
        assertNotNull(result);
        assertEquals("attr-123", result.id().value());
        assertEquals("def-123", result.attribute_definition_id().value());
    }

    @Test
    void shouldReturnNullWhenCategoryAttributeNotFound() {
        Id id = Id.fromString("def-123");
        when(jdbcTemplate.query(anyString(), any(CategoryAttributeResultSetExtractor.class), eq("def-123")))
            .thenReturn(null);

        CategoryAttribute result = repositoryAdapter.getCategoryAttributeByAttributeDefinitionId(id);

        assertNull(result);
    }

    @Test
    void shouldGetCategoryAttributeByIdSuccessfully() {
        Id id = Id.fromString("attr-123");
        CategoryAttributeEntity entity = new CategoryAttributeEntity(
            "attr-123", "cat-123", "def-123", null, true, true, true
        );

        when(aggregateTemplate.findById(eq("attr-123"), eq(CategoryAttributeEntity.class)))
            .thenReturn(entity);

        CategoryAttribute result = repositoryAdapter.getCategoryAttributeById(id);

        assertNotNull(result);
        assertEquals("attr-123", result.id().value());
    }

    @Test
    void shouldThrowDataNotFoundWhenCategoryAttributeByIdNotFound() {
        Id id = Id.fromString("attr-123");
        when(aggregateTemplate.findById(eq("attr-123"), eq(CategoryAttributeEntity.class)))
            .thenReturn(null);

        assertThrows(DataNotFound.class, () -> repositoryAdapter.getCategoryAttributeById(id));
    }

    @Test
    void shouldFindBySlugSuccessfully() {
        Slug slug = Slug.fromString("electronics");
        CategoryEntity entity = createCategoryEntity();

        when(categoryJdbcRepository.findBySlugAndStatus(slug.value(), Status.ENABLED.name()))
            .thenReturn(Optional.of(entity));

        Category result = repositoryAdapter.findBySlug(slug);

        assertNotNull(result);
        assertEquals("cat-123", result.id().value());
    }

    @Test
    void shouldFindByIdSuccessfully() {
        Id id = Id.fromString("cat-123");
        CategoryEntity entity = createCategoryEntity();

        when(categoryJdbcRepository.findById(id.value()))
            .thenReturn(Optional.of(entity));

        Category result = repositoryAdapter.findById(id);

        assertNotNull(result);
        assertEquals("cat-123", result.id().value());
    }

    @Test
    void shouldSaveCategorySuccessfully() {
        Category category = createDomainCategory();

        when(attributeDefinitionJdbcRepository.countByIdIn(anySet())).thenReturn(0L);
        when(categoryJdbcRepository.existsBySlug(category.slug().value())).thenReturn(false);
        when(categoryJdbcRepository.existsByName(category.name())).thenReturn(false);

        assertDoesNotThrow(() -> repositoryAdapter.save(category));

        verify(aggregateTemplate, times(1)).insert(any(CategoryEntity.class));
    }

    @Test
    void shouldThrowExceptionOnSaveWhenSlugExists() {
        Category category = createDomainCategory();

        when(attributeDefinitionJdbcRepository.countByIdIn(anySet())).thenReturn(0L);
        when(categoryJdbcRepository.existsBySlug(category.slug().value())).thenReturn(true);

        assertThrows(RuntimeException.class, () -> repositoryAdapter.save(category));
        verify(aggregateTemplate, never()).insert(any(CategoryEntity.class));
    }

    @Test
    void shouldUpdateCategorySuccessfully() {
        Category category = createDomainCategory();
        CategoryEntity entity = createCategoryEntity();

        when(attributeDefinitionJdbcRepository.countByIdIn(anySet())).thenReturn(0L);
        when(aggregateTemplate.findById(category.id().value(), CategoryEntity.class)).thenReturn(entity);

        assertDoesNotThrow(() -> repositoryAdapter.update(category));

        verify(aggregateTemplate, times(1)).update(any(CategoryEntity.class));
    }

    @Test
    void shouldDeleteCategorySuccessfully() {
        Category category = createDomainCategory();

        assertDoesNotThrow(() -> repositoryAdapter.delete(category));

        verify(aggregateTemplate, times(1)).delete(any(CategoryEntity.class));
    }
}
