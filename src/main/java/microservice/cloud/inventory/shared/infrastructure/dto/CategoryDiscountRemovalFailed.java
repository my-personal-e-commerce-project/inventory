package microservice.cloud.inventory.shared.infrastructure.dto;

import java.time.LocalDateTime;

public record CategoryDiscountRemovalFailed (
    String categoryId,
    LocalDateTime occurredOn
) {
    public CategoryDiscountRemovalFailed(String categoryId) {
        this(categoryId, LocalDateTime.now());
    }

    @Override
    public LocalDateTime occurredOn() {
        return occurredOn;
    }
}
