package microservice.cloud.inventory.category.domain.entity;

import microservice.cloud.inventory.attribute.domain.entity.AttributeDefinition;
import microservice.cloud.inventory.attribute.domain.value_objects.DataType;
import microservice.cloud.inventory.shared.domain.value_objects.Id;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CategoryAttributeTest {

    @Test
    void shouldCreateCategoryAttributeWithValidValues() {
        Id id = Id.generate();
        Id defId = Id.generate();

        CategoryAttribute categoryAttribute = new CategoryAttribute(
            id,
            defId,
            true,
            false,
            true
        );

        assertEquals(id, categoryAttribute.id());
        assertEquals(defId, categoryAttribute.attribute_definition_id());
        assertTrue(categoryAttribute.is_required());
        assertFalse(categoryAttribute.is_filterable());
        assertTrue(categoryAttribute.is_sortable());
        assertNull(categoryAttribute.attribute_definition());
    }

    @Test
    void shouldThrowExceptionWhenIdIsNull() {
        Id defId = Id.generate();
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> new CategoryAttribute(null, defId, true, true, true)
        );
        assertEquals("The id cannot be null", exception.getMessage());
    }

    @Test
    void shouldLoadNonGlobalAttributeDefinition() {
        CategoryAttribute categoryAttribute = new CategoryAttribute(
            Id.generate(),
            Id.generate(),
            true,
            true,
            true
        );

        AttributeDefinition nonGlobalDef = new AttributeDefinition(
            Id.generate(),
            "Non Global",
            Slug.fromString("non-global"),
            DataType.STRING,
            false
        );

        assertDoesNotThrow(() -> categoryAttribute.load_attribute_definition(nonGlobalDef));
        assertEquals(nonGlobalDef, categoryAttribute.attribute_definition());
    }

    @Test
    void shouldThrowExceptionWhenLoadingGlobalAttributeDefinition() {
        CategoryAttribute categoryAttribute = new CategoryAttribute(
            Id.generate(),
            Id.generate(),
            true,
            true,
            true
        );

        AttributeDefinition globalDef = new AttributeDefinition(
            Id.generate(),
            "Global",
            Slug.fromString("global"),
            DataType.STRING,
            true
        );

        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> categoryAttribute.load_attribute_definition(globalDef)
        );
        assertEquals("The attribute definition cannot be global attribute.", exception.getMessage());
    }

    @Test
    void testEqualsAndHashCode() {
        Id defId1 = Id.generate();
        Id defId2 = Id.generate();

        CategoryAttribute attr1 = new CategoryAttribute(Id.generate(), defId1, true, true, true);
        CategoryAttribute attr2 = new CategoryAttribute(Id.generate(), defId1, false, false, false);
        CategoryAttribute attr3 = new CategoryAttribute(Id.generate(), defId2, true, true, true);

        assertEquals(attr1, attr2);
        assertNotEquals(attr1, attr3);
        assertEquals(attr1.hashCode(), attr2.hashCode());
        assertNotEquals(attr1.hashCode(), attr3.hashCode());
    }
}
