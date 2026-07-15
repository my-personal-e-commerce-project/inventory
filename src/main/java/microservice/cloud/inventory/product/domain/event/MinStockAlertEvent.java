package microservice.cloud.inventory.product.domain.event;

import java.time.LocalDateTime;

import microservice.cloud.inventory.shared.domain.event.DomainEvent;

public record MinStockAlertEvent(
    LocalDateTime occurredOn,
    String aggregateId,
    Integer currentStock
) implements DomainEvent {

    public MinStockAlertEvent(
        String aggregateId,
        Integer currentStock
    ) {

        this(
            LocalDateTime.now(),
            aggregateId,
            currentStock
        );
    }
}
