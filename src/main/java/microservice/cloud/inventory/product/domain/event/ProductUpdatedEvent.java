package microservice.cloud.inventory.product.domain.event;

import java.time.Instant;
import java.util.List;

import microservice.cloud.inventory.product.domain.entity.ProductAttributeValue;
import microservice.cloud.inventory.shared.domain.event.DomainEvent;

public record ProductUpdatedEvent(
        String id,
        String title,
        String slug,
        String description,
        List<String> categories,
        double price,
        int stock,
        List<String> images,
        List<ProductAttributeValue> attributes,
        Instant occurredOn
) implements DomainEvent {

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
        List<ProductAttributeValue> attributes
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
            attributes,
            Instant.now()
        );
    }
}

