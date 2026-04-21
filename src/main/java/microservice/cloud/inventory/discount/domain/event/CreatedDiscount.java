package microservice.cloud.inventory.discount.domain.event;

import java.time.LocalDateTime;
import java.util.Set;

import microservice.cloud.inventory.shared.domain.event.DomainEvent;

public record CreatedDiscount(
    String aggregateId,
    String name, 
    String discountType,
    Double percentageValue,
    Double decrementValue,
    Set<String> allowedCategories,
    boolean validAllCategories,
    Double minPrice,
    Double maxPrice,
    Integer minStock,
    Integer maxStock,
    boolean autoApply,
    LocalDateTime expiredAt
) implements DomainEvent {

    @Override
    public LocalDateTime occurredOn() {
        return LocalDateTime.now();
    }
}
