package microservice.cloud.inventory.productStock.domain.entity;

import microservice.cloud.inventory.product.domain.value_objects.Quantity;
import microservice.cloud.inventory.shared.domain.value_objects.Id;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductStockTest {

    @Test
    void shouldCreateProductStockWithValidValues() {
        Id id = Id.generate();
        Quantity quantity = new Quantity(50);
        ProductStock productStock = new ProductStock(id, quantity);

        assertEquals(id, productStock.id());
        assertEquals(quantity, productStock.quantity());
        assertEquals(50, productStock.quantity().value());
    }

    @Test
    void shouldDecrementQuantitySuccessfully() {
        Id id = Id.generate();
        ProductStock productStock = new ProductStock(id, new Quantity(50));

        productStock.decrementQuantity(10);

        assertEquals(40, productStock.quantity().value());
    }

    @Test
    void shouldDecrementQuantityToZero() {
        Id id = Id.generate();
        ProductStock productStock = new ProductStock(id, new Quantity(10));

        productStock.decrementQuantity(10);

        assertEquals(0, productStock.quantity().value());
    }

    @Test
    void shouldThrowExceptionWhenDecrementingQuantityBelowZero() {
        Id id = Id.generate();
        ProductStock productStock = new ProductStock(id, new Quantity(5));

        assertThrows(IllegalArgumentException.class, () -> productStock.decrementQuantity(10));
    }

    @Test
    void shouldUpdateQuantitySuccessfully() {
        Id id = Id.generate();
        ProductStock productStock = new ProductStock(id, new Quantity(50));
        Quantity newQuantity = new Quantity(100);

        productStock.updateQuantity(newQuantity);

        assertEquals(newQuantity, productStock.quantity());
        assertEquals(100, productStock.quantity().value());
    }
}
