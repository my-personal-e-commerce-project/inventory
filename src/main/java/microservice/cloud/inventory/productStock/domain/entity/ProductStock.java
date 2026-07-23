package microservice.cloud.inventory.productStock.domain.entity;

import microservice.cloud.inventory.product.domain.value_objects.Quantity;
import microservice.cloud.inventory.shared.domain.value_objects.Id;

public class ProductStock {
    private Id id;
    private Id productId;
    private Quantity quantity;

    public ProductStock(Id id, Id productId, Quantity quantity) {
        if (id == null) throw new IllegalArgumentException("Id is required");
        if (productId == null) throw new IllegalArgumentException("Product id is required");
        if (quantity == null) throw new IllegalArgumentException("Quantity is required");

        this.id = id;
        this.productId = productId;
        this.quantity = quantity;
    }

    public void decrementQuantity(int value) {
        this.quantity = this.quantity.decrementValue(value);
    }

    public void updateQuantity(Quantity quantity) {
        this.quantity = quantity;
    }

    public Id id() {
        return id;
    }

    public Id productId() {
        return productId;
    }

    public Quantity quantity() {
        return quantity;
    }
}
