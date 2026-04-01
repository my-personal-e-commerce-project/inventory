package microservice.cloud.inventory.shared.domain.event;

import java.time.LocalDateTime;

public record DomainEvent(
    LocalDateTime occurredOn,
    String aggregateId
) {
    public DomainEvent(String aggregateId) {
        this(LocalDateTime.now(), aggregateId);
    }
}
