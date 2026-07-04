package microservice.cloud.inventory.attribute.domain.entity;

import java.util.List;

import microservice.cloud.inventory.attribute.domain.event.CreatedGlobalAttributeDefinition;
import microservice.cloud.inventory.attribute.domain.value_objects.DataType;
import microservice.cloud.inventory.shared.domain.event.DomainEvent;
import microservice.cloud.inventory.shared.domain.value_objects.Id;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AttributeDefinitionTest {

    @Test
    void shouldCreateAttributeDefinitionWithValidValues() {
        Id id = Id.generate();
        Slug slug = Slug.fromString("color");
        AttributeDefinition def = new AttributeDefinition(id, "Color", slug, DataType.STRING, false);

        assertEquals(id, def.id());
        assertEquals("Color", def.name());
        assertEquals(slug, def.slug());
        assertEquals(DataType.STRING, def.type());
        assertFalse(def.is_global());
    }

    @Test
    void shouldThrowExceptionWhenNameIsNull() {
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> new AttributeDefinition(Id.generate(), null, Slug.fromString("color"), DataType.STRING, false)
        );
        assertEquals("The name cannot be null", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenTypeIsNull() {
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> new AttributeDefinition(Id.generate(), "Color", Slug.fromString("color"), null, false)
        );
        assertEquals("The type cannot be null", exception.getMessage());
    }

    @Test
    void shouldCreateViaFactoryAndPublishEventWhenGlobal() {
        Id id = Id.generate();
        Slug slug = Slug.fromString("brand");
        AttributeDefinition def = AttributeDefinition.factory(id, "Brand", slug, DataType.STRING, true);

        assertEquals(id, def.id());
        assertTrue(def.is_global());

        List<DomainEvent> events = def.getEvents();
        assertEquals(1, events.size());
        assertTrue(events.get(0) instanceof CreatedGlobalAttributeDefinition);
        CreatedGlobalAttributeDefinition event = (CreatedGlobalAttributeDefinition) events.get(0);
        assertEquals(id.value(), event.aggregateId());
        assertEquals("Brand", event.name());
        assertEquals("brand", event.slug());
        assertEquals("STRING", event.type());
        assertTrue(event.is_global());
    }

    @Test
    void shouldCreateViaFactoryAndNotPublishEventWhenNotGlobal() {
        Id id = Id.generate();
        Slug slug = Slug.fromString("color");
        AttributeDefinition def = AttributeDefinition.factory(id, "Color", slug, DataType.STRING, false);

        assertFalse(def.is_global());
        assertTrue(def.getEvents().isEmpty());
    }

    @Test
    void shouldUpdateSuccessfullyWhenGlobalFlagNotChanged() {
        AttributeDefinition def = new AttributeDefinition(Id.generate(), "Color", Slug.fromString("color"), DataType.STRING, false);
        Slug newSlug = Slug.fromString("new-color");

        def.update("New Color", newSlug, DataType.INTEGER, false);

        assertEquals("New Color", def.name());
        assertEquals(newSlug, def.slug());
        assertEquals(DataType.INTEGER, def.type());
        assertFalse(def.is_global());
    }

    @Test
    void shouldThrowExceptionOnUpdateWhenGlobalFlagChanges() {
        AttributeDefinition def = new AttributeDefinition(Id.generate(), "Color", Slug.fromString("color"), DataType.STRING, false);

        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> def.update("New Color", Slug.fromString("color"), DataType.STRING, true)
        );
        assertEquals("Can you not change the value of is_global.", exception.getMessage());
    }
}
