package microservice.cloud.inventory.product.infrastructure.presentation.validate;

public record ProductStockDTO(
    int stock,
    boolean isIncreasing
) {}
