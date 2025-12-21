package microservice.cloud.inventory.shared.infrastructure.event;

import java.time.Instant;

public interface BaseEvent {
    String eventName();
    String aggregateId();
    Instant occurredOn();
}
