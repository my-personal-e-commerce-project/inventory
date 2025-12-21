package microservice.cloud.inventory.product.infrastructure.dtos.event;

import java.time.Instant;
import java.util.List;

import microservice.cloud.inventory.product.domain.entity.ProductAttributeValue;
import microservice.cloud.inventory.shared.infrastructure.event.BaseEvent;

public record ProductUpdatedEvent(
        String id,
        String title,
        String slug,
        String description,
        List<String> categories,
        double price,
        int stock,
        List<String> images,
        List<ProductAttributeValueEvent> attributes,
        List<String> tags,
        Instant occurredOn
) implements BaseEvent {

    @Override
    public String aggregateId() {
        return id;
    }

    @Override
    public String eventName() {
        return "product.updated";
    }

    public ProductUpdatedEvent(
        String id,
        String title,
        String slug,
        String description,
        List<String> categories,
        double price,
        int stock,
        List<String> images,
        List<ProductAttributeValue> attributes,
        List<String> tags
    ) {

      this(
            id,
            title,
            slug,
            description,
            categories,
            price,
            stock,
            images,
            attributes.stream().map(a -> {
                return new ProductAttributeValueEvent(
                    a.attribute_definition().value(),
                    a.string_value(), 
                    a.integer_value(), 
                    a.double_value(), 
                    a.boolean_value()
                );
            }).toList(),
            tags,
            Instant.now()
        );
    }
}

