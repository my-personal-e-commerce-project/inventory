package microservice.cloud.inventory.category.domain.event;

import java.time.LocalDateTime;

import microservice.cloud.inventory.shared.domain.event.DomainEvent;

public record DeletedCategory(
    LocalDateTime occurredOn,
    String aggregateId
) implements DomainEvent {

    public DeletedCategory(
        String aggregateId
    ) {
        this(
            LocalDateTime.now(),
            aggregateId
        );
    }
}
