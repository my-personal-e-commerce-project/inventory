package microservice.cloud.inventory.product.application.dtos;

import java.util.List;

public record QueryProducts(
    String search,
    List<String> categories,
    Double minPrice,
    Double maxPrice,
    Integer minStock,
    Integer maxStock,
    Boolean isActive
) {}
