package microservice.cloud.inventory.category.domain.event;

import java.time.LocalDateTime;

import microservice.cloud.inventory.shared.domain.event.DomainEvent;

public record CreatedCategoryAttribute(
    LocalDateTime occurredOn,
    String aggregateId,
    String id,
    String attribute_definition_id,
    Boolean is_required,
    Boolean is_filterable,
    Boolean is_sortable
) implements DomainEvent {

    public CreatedCategoryAttribute(
        String aggregateId,
        String id,
        String attribute_definition_id,
        Boolean is_required,
        Boolean is_filterable,
        Boolean is_sortable
    ) {
        this(
            LocalDateTime.now(),
            aggregateId,
            id,
            attribute_definition_id, 
            is_required, 
            is_filterable, 
            is_sortable
        );
    }
}
