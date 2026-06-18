package microservice.cloud.inventory.shared.infrastructure.dto;

import java.time.LocalDateTime;

public record CategoryDiscountRemoved (
    String categoryId,
    boolean success,
    String errorMessage,
    LocalDateTime occurredOn
) {}
