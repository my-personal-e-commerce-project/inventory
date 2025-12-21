package microservice.cloud.inventory.product.infrastructure.dtos.event;

import java.time.Instant;

import microservice.cloud.inventory.shared.infrastructure.event.BaseEvent;

public record ProductDeletedEvent(
    String id,
    Instant occurredOn
) implements BaseEvent {

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

