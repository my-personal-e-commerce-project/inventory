package microservice.cloud.inventory.discount.domain.value_objects;

public record Percentage(
    Double value
) {
    public Percentage(Double value) {
        if(value > 100)
            throw new RuntimeException("The percentage cannot be greater than 100%.");

        if(value < 0)
            throw new RuntimeException("The percentage cannot be a negative number.");

        this.value = value;
    }
}
