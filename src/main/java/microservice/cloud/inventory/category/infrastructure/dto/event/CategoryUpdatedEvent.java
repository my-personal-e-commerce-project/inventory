package microservice.cloud.inventory.category.infrastructure.dto.event;

import java.time.Instant;
import java.util.List;

import microservice.cloud.inventory.category.domain.entity.CategoryAttribute;
import microservice.cloud.inventory.shared.infrastructure.event.BaseEvent;

public record CategoryUpdatedEvent(
        String id,
        String name,
        String slug,
        String parent_id,
        List<CategoryAttributeEvent> attributes,
        Instant occurredOn
        ) implements BaseEvent {

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
                        new AttributeDefinitionEvent(
                            a.attribute_definition().id().value(),
                            a.attribute_definition().name(),
                            a.attribute_definition().slug().value(),
                            a.attribute_definition().type().toString()
                            ),
                        a.is_required(),
                        a.is_filterable(),
                        a.is_sortable()
                        ))
                .toList(),
                Instant.now()
            );
            } 
        }
