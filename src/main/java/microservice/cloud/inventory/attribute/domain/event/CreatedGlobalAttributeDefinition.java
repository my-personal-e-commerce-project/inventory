package microservice.cloud.inventory.attribute.domain.event;

import java.time.LocalDateTime;

import microservice.cloud.inventory.shared.domain.event.DomainEvent;

public record CreatedGlobalAttributeDefinition(
    String aggregateId,
    String name,
    String slug,
    String type,
    boolean is_global
) implements DomainEvent {

    @Override
    public LocalDateTime occurredOn() {
        return LocalDateTime.now();
    }
}
