package microservice.cloud.inventory.shared.infrastructure.dto;

import java.time.LocalDateTime;

public record CategoryDiscountRemoved (
    String categoryId,
    LocalDateTime occurredOn
) {
    public CategoryDiscountRemoved(String categoryId) {
        this(categoryId, LocalDateTime.now());
    }

    @Override
    public LocalDateTime occurredOn() {
        return occurredOn;
    }
}
