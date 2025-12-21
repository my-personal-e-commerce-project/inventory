package microservice.cloud.inventory.product.domain.value_objects;

public class Price {

    private double value;
    
    public Price(double value) {
        if(value < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }

        this.value = value;
    }

    public Double value() {
        return value;
    }
}
