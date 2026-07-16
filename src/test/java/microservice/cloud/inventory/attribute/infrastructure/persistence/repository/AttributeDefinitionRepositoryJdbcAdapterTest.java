package microservice.cloud.inventory.attribute.infrastructure.persistence.repository;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;

import microservice.cloud.inventory.attribute.domain.entity.AttributeDefinition;
import microservice.cloud.inventory.attribute.domain.value_objects.DataType;
import microservice.cloud.inventory.attribute.infrastructure.persistence.model.AttributeDefinitionEntity;
import microservice.cloud.inventory.shared.domain.exception.DataNotFound;
import microservice.cloud.inventory.shared.domain.value_objects.Id;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttributeDefinitionRepositoryJdbcAdapterTest {

    @Mock
    private AttributeDefinitionJdbcRepository attributeDefinitionJdbcRepository;

    @Mock
    private JdbcAggregateTemplate aggregateTemplate;

    @InjectMocks
    private AttributeDefinitionRepositoryJdbcAdapter adapter;

    @Test
    void shouldGetGlobalAttributesSuccessfully() {
        AttributeDefinitionEntity entity = new AttributeDefinitionEntity("id-123", "Global", "global", "STRING", true);
        when(attributeDefinitionJdbcRepository.findAllByIsGlobal(true)).thenReturn(List.of(entity));

        List<AttributeDefinition> result = adapter.getGlobalAttributes();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("id-123", result.get(0).id().value());
        assertTrue(result.get(0).is_global());
    }

    @Test
    void shouldGetByIdSuccessfully() {
        Id id = Id.generate();
        AttributeDefinitionEntity entity = new AttributeDefinitionEntity(id.value(), "Name", "slug", "STRING", false);
        when(aggregateTemplate.findById(id.value(), AttributeDefinitionEntity.class)).thenReturn(entity);

        AttributeDefinition result = adapter.getById(id);

        assertNotNull(result);
        assertEquals(id.value(), result.id().value());
        assertEquals("Name", result.name());
    }

    @Test
    void shouldThrowDataNotFoundWhenGetByIdNotFound() {
        Id id = Id.generate();
        when(aggregateTemplate.findById(id.value(), AttributeDefinitionEntity.class)).thenReturn(null);

        assertThrows(DataNotFound.class, () -> adapter.getById(id));
    }

    @Test
    void shouldValidateAttributeDefinitionIdsSuccessfully() {
        String id1 = Id.generate().value();
        AttributeDefinitionEntity entity = new AttributeDefinitionEntity(id1, "Name", "slug", "STRING", false);
        when(attributeDefinitionJdbcRepository.findAllByIdIn(Set.of(id1))).thenReturn(List.of(entity));

        assertDoesNotThrow(() -> adapter.isValidTheseAttributeDefinitionIds(new HashSet<>(Set.of(id1))));
    }

    @Test
    void shouldThrowExceptionWhenValidatingNonExistentIds() {
        String id1 = "non-existent-id";
        when(attributeDefinitionJdbcRepository.findAllByIdIn(Set.of(id1))).thenReturn(List.of());

        assertThrows(RuntimeException.class, () -> adapter.isValidTheseAttributeDefinitionIds(new HashSet<>(Set.of(id1))));
    }

    @Test
    void shouldGetBySlugSuccessfully() {
        Slug slug = Slug.fromString("some-slug");
        AttributeDefinitionEntity entity = new AttributeDefinitionEntity("id-123", "Name", slug.value(), "STRING", false);
        when(attributeDefinitionJdbcRepository.findBySlug(slug.value())).thenReturn(entity);

        AttributeDefinition result = adapter.getBySlug(slug);

        assertNotNull(result);
        assertEquals("id-123", result.id().value());
        assertEquals(slug.value(), result.slug().value());
    }

    @Test
    void shouldThrowDataNotFoundWhenGetBySlugNotFound() {
        Slug slug = Slug.fromString("some-slug");
        when(attributeDefinitionJdbcRepository.findBySlug(slug.value())).thenReturn(null);

        assertThrows(DataNotFound.class, () -> adapter.getBySlug(slug));
    }

    @Test
    void shouldFindByIdsSuccessfully() {
        String id1 = Id.generate().value();
        AttributeDefinitionEntity entity = new AttributeDefinitionEntity(id1, "Name", "slug", "STRING", false);
        when(attributeDefinitionJdbcRepository.findAllByIdIn(Set.of(id1))).thenReturn(List.of(entity));

        Map<String, AttributeDefinition> result = adapter.findByIds(Set.of(id1));

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.containsKey(id1));
    }

    @Test
    void shouldSaveSuccessfully() {
        AttributeDefinition attr = new AttributeDefinition(Id.generate(), "Name", Slug.fromString("slug"), DataType.STRING, false);
        when(attributeDefinitionJdbcRepository.existsBySlug(attr.slug().value())).thenReturn(false);

        assertDoesNotThrow(() -> adapter.save(attr));
        verify(aggregateTemplate, times(1)).insert(any(AttributeDefinitionEntity.class));
    }

    @Test
    void shouldThrowExceptionWhenSavingDuplicateSlug() {
        AttributeDefinition attr = new AttributeDefinition(Id.generate(), "Name", Slug.fromString("slug"), DataType.STRING, false);
        when(attributeDefinitionJdbcRepository.existsBySlug(attr.slug().value())).thenReturn(true);

        assertThrows(RuntimeException.class, () -> adapter.save(attr));
        verify(aggregateTemplate, never()).insert(any(AttributeDefinitionEntity.class));
    }

    @Test
    void shouldUpdateSuccessfully() {
        AttributeDefinition attr = new AttributeDefinition(Id.generate(), "Name", Slug.fromString("slug"), DataType.STRING, false);

        assertDoesNotThrow(() -> adapter.update(attr));
        verify(aggregateTemplate, times(1)).update(any(AttributeDefinitionEntity.class));
    }

    @Test
    void shouldDeleteSuccessfully() {
        AttributeDefinition attr = new AttributeDefinition(Id.generate(), "Name", Slug.fromString("slug"), DataType.STRING, false);

        assertDoesNotThrow(() -> adapter.delete(attr));
        verify(aggregateTemplate, times(1)).delete(any(AttributeDefinitionEntity.class));
    }
}
