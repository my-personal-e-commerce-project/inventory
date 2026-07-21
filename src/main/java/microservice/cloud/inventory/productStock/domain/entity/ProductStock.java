package microservice.cloud.inventory.productStock.domain.entity;

import microservice.cloud.inventory.product.domain.value_objects.Quantity;
import microservice.cloud.inventory.shared.domain.value_objects.Id;

public class ProductStock {
    private Id id;
    private Quantity quantity;

    public ProductStock(Id id, Quantity quantity) {
        this.id = id;
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

    public Quantity quantity() {
        return quantity;
    }
}
