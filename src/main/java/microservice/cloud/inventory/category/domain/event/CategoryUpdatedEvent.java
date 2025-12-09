package microservice.cloud.inventory.category.domain.event;

import java.time.Instant;
import java.util.List;

import microservice.cloud.inventory.category.domain.entity.CategoryAttribute;
import microservice.cloud.inventory.shared.domain.event.DomainEvent;

public record CategoryUpdatedEvent(
        String id,
        String name,
        String slug,
        String parent_id,
        List<CategoryAttributeEvent> attributes,
        Instant occurredOn
) implements DomainEvent {

    @Override
    public String aggregateId() {
        return id;
    }

    @Override
    public String eventName() {
        return "category.updated";
    }

    public CategoryUpdatedEvent(
        String id,
        String name,
        String slug,
        String parent_id,
        List<CategoryAttribute> attributes
    ) {
        this(
            id,
            name,
            slug,
            parent_id,
            attributes
                .stream()
                .map((a) -> new CategoryAttributeEvent(
                    a.attribute_definition().name(), 
                    a.attribute_definition().slug().value(),
                    a.attribute_definition().type().toString(),
                    a.is_required(),
                    a.is_filterable(),
                    a.is_sortable()
                ))
                .toList(),
            Instant.now()
        );
    } 
}
