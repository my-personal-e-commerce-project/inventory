package microservice.cloud.inventory.product.domain.event;

import java.time.Instant;

import microservice.cloud.inventory.shared.domain.event.DomainEvent;

public record ProductDeletedEvent(
    String id,
    Instant occurredOn
) implements DomainEvent {

    @Override
    public String aggregateId() {
        return id;
    }
    
    @Override
    public String eventName() {
        return "product.deleted";
    }

    public ProductDeletedEvent(
        String id
    ) {

        this(
            id,
            Instant.now()
        );
    }
}

