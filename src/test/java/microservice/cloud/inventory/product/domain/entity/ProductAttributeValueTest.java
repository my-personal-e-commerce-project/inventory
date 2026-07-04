package microservice.cloud.inventory.product.domain.entity;

import microservice.cloud.inventory.attribute.domain.entity.AttributeDefinition;
import microservice.cloud.inventory.attribute.domain.value_objects.DataType;
import microservice.cloud.inventory.shared.domain.value_objects.Id;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProductAttributeValueTest {

    private AttributeDefinition createAttributeDefinition(DataType dataType) {
        return new AttributeDefinition(
            Id.generate(),
            "Test Attribute",
            Slug.fromString("test-attribute"),
            dataType,
            false
        );
    }

    @Test
    void shouldPassValidationWhenStringValueIsNotNull() {
        AttributeDefinition def = createAttributeDefinition(DataType.STRING);
        ProductAttributeValue pav = new ProductAttributeValue(
            Id.generate(),
            def.id(),
            "Some Value",
            null,
            null,
            null
        );

        assertDoesNotThrow(() -> pav.validTypes(def));
    }


    @Test
    void shouldThrowExceptionWhenAtLeastOneValueMustBeNotNull() {
        AttributeDefinition def = createAttributeDefinition(DataType.STRING); 

        RuntimeException exception = assertThrows(
            RuntimeException.class, 
            () -> new ProductAttributeValue(
                Id.generate(),
                def.id(),
                null,
                null,
                null,
                null
            )
        );
        assertTrue(exception.getMessage().contains("At least one value must be not null"));
    }

    @Test
    void shouldThrowExceptionWhenStringValueIsNull() {
        AttributeDefinition def = createAttributeDefinition(DataType.STRING);
        ProductAttributeValue pav = new ProductAttributeValue(
            Id.generate(),
            def.id(),
            null,
            2,
            null,
            null
        );

        RuntimeException exception = assertThrows(
            RuntimeException.class, 
            () -> pav.validTypes(def)
        );
        assertTrue(exception.getMessage().contains("String value cannot be null"));
    }

    @Test
    void shouldPassValidationWhenIntegerValueIsNotNull() {
        AttributeDefinition def = createAttributeDefinition(DataType.INTEGER);
        ProductAttributeValue pav = new ProductAttributeValue(
            Id.generate(),
            def.id(),
            null,
            42,
            null,
            null
        );

        assertDoesNotThrow(() -> pav.validTypes(def));
    }

    @Test
    void shouldThrowExceptionWhenIntegerValueIsNull() {
        AttributeDefinition def = createAttributeDefinition(DataType.INTEGER);
        ProductAttributeValue pav = new ProductAttributeValue(
            Id.generate(),
            def.id(),
            "",
            null,
            null,
            null
        );

        RuntimeException exception = assertThrows(
            RuntimeException.class, 
            () -> pav.validTypes(def)
        );
        assertTrue(exception.getMessage().contains("Integer value cannot be null"));
    }

    @Test
    void shouldPassValidationWhenDoubleValueIsNotNull() {
        AttributeDefinition def = createAttributeDefinition(DataType.DOUBLE);
        ProductAttributeValue pav = new ProductAttributeValue(
            Id.generate(),
            def.id(),
            null,
            null,
            99.9,
            null
        );

        assertDoesNotThrow(() -> pav.validTypes(def));
    }

    @Test
    void shouldThrowExceptionWhenDoubleValueIsNull() {
        AttributeDefinition def = createAttributeDefinition(DataType.DOUBLE);
        ProductAttributeValue pav = new ProductAttributeValue(
            Id.generate(),
            def.id(),
            "",
            null,
            null,
            null
        );

        RuntimeException exception = assertThrows(
            RuntimeException.class, 
            () -> pav.validTypes(def)
        );
        assertTrue(exception.getMessage().contains("Double value cannot be null"));
    }

    @Test
    void shouldPassValidationWhenBooleanValueIsNotNull() {
        AttributeDefinition def = createAttributeDefinition(DataType.BOOLEAN);
        ProductAttributeValue pav = new ProductAttributeValue(
            Id.generate(),
            def.id(),
            null,
            null,
            null,
            true
        );

        assertDoesNotThrow(() -> pav.validTypes(def));
    }

    @Test
    void shouldThrowExceptionWhenBooleanValueIsNull() {
        AttributeDefinition def = createAttributeDefinition(DataType.BOOLEAN);
        ProductAttributeValue pav = new ProductAttributeValue(
            Id.generate(),
            def.id(),
            "",
            null,
            null,
            null
        );

        RuntimeException exception = assertThrows(
            RuntimeException.class, 
            () -> pav.validTypes(def)
        );
        assertTrue(exception.getMessage().contains("Boolean value cannot be null"));
    }
}
