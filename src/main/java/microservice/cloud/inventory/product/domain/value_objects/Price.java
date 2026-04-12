package microservice.cloud.inventory.product.domain.value_objects;

public record Price (
    Double value
) {
    public Price(Double value) {
        if(value < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }

        this.value = value;
    }

    public boolean isLessThan(Price other) {
        return this.value < other.value();
    }

    public boolean isGreater(Price other) {
        return this.value > other.value();
    }

    public Price decrementValue(Double decrementAmount) {

        return new Price(value - decrementAmount);
    }
}
