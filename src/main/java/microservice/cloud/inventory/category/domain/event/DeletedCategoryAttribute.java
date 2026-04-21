package microservice.cloud.inventory.category.domain.event;

import java.time.LocalDateTime;

import microservice.cloud.inventory.shared.domain.event.DomainEvent;

public record DeletedCategoryAttribute(
    LocalDateTime occurredOn,
    String aggregateId,
    String attribute_definition_id
) implements DomainEvent {

    public DeletedCategoryAttribute(
        String aggregateId,
        String attribute_definition_id
    ) {
        this(
            LocalDateTime.now(),
            aggregateId,
            attribute_definition_id
        );
    }
}
