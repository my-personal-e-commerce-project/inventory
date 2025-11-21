package microservice.cloud.inventory.product.domain.value_objects;

public class Quantity {

    private Integer value;

    public Quantity(Integer value) {
        if(value < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }

        this.value = value;
    }

    public Integer value() {
        return value;
    }
}
