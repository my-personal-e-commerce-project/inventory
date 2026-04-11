package microservice.cloud.inventory.discount.application.dtos;

import java.time.LocalDateTime;
import java.util.List;

public record DiscountReadDTO(
    String id,
    String name,
    String discountType,
    Double percentageValue,
    Double decrementValue,
    List<String> allowedCategories,
    boolean validAllCategories,
    Double minPrice,
    Double maxPrice,
    int minStock,
    int maxStock,
    boolean autoApply,
    LocalDateTime expiredAt
) {}
