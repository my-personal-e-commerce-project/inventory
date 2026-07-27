package microservice.cloud.inventory.product.domain.value_objects;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class QuantityTest {

    @Test
    void shouldCreateValidQuantity() {
        Quantity quantity = new Quantity(5);
        assertEquals(5, quantity.value());
    }

    @Test
    void shouldCreateZeroQuantity() {
        Quantity quantity = new Quantity(0);
        assertEquals(0, quantity.value());
    }

    @Test
    void shouldThrowExceptionWhenQuantityIsNegative() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class, 
            () -> new Quantity(-5)
        );
        assertEquals("Price cannot be negative", exception.getMessage());
    }
}
