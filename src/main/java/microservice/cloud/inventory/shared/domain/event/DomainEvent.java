package microservice.cloud.inventory.shared.domain.event;

import java.time.Instant;

public interface DomainEvent {
    String eventName();
    Instant occurredOn();
}
