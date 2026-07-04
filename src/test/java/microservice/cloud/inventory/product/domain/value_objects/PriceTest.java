package microservice.cloud.inventory.product.domain.value_objects;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PriceTest {

    @Test
    void shouldCreateValidPrice() {
        Price price = new Price(10.5);
        assertEquals(10.5, price.value());
    }

    @Test
    void shouldCreateZeroPrice() {
        Price price = new Price(0.0);
        assertEquals(0.0, price.value());
    }

    @Test
    void shouldThrowExceptionWhenPriceIsNegative() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class, 
            () -> new Price(-1.0)
        );
        assertEquals("Price cannot be negative", exception.getMessage());
    }

    @Test
    void shouldComparePricesCorrectly() {
        Price low = new Price(5.0);
        Price high = new Price(10.0);

        assertTrue(low.isLessThan(high));
        assertFalse(high.isLessThan(low));

        assertTrue(high.isGreater(low));
        assertFalse(low.isGreater(high));
    }

    @Test
    void shouldDecrementValueCorrectly() {
        Price original = new Price(100.0);
        Price decrement = new Price(30.0);
        Price result = original.decrementValue(decrement);

        assertEquals(70.0, result.value());
    }
}
