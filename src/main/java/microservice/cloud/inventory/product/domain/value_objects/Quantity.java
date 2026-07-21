package microservice.cloud.inventory.product.domain.value_objects;

public record Quantity (
    int value
){
    public Quantity(int value) {
        if(value < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }

        this.value = value;
    }

    public boolean isLessThan(Quantity other) {
        return this.value < other.value();
    }

    public Quantity decrementValue(int value) {
        return new Quantity(this.value - value);
    }
}
