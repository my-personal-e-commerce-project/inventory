package microservice.cloud.inventory.category.infrastructure.dto.event;

import java.time.Instant;

import microservice.cloud.inventory.shared.infrastructure.event.BaseEvent;

public record CategoryDeletedEvent(
    String id,
    Instant occurredOn
) implements BaseEvent {

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
