package microservice.cloud.inventory.category.domain.event;

import java.time.Instant;

import microservice.cloud.inventory.shared.domain.event.DomainEvent;

public record CategoryDeletedEvent(
    String id,
    Instant occurredOn
) implements DomainEvent {

    @Override
    public String aggregateId() {
        return id;
    }

    @Override
    public String eventName() {
        return "category.deleted";
    }

    public CategoryDeletedEvent(
        String id
    ) {
        this(
            id,
            Instant.now()
        );
    } 
}
