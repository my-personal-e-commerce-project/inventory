package microservice.cloud.inventory.shared.application.ports.out;

import java.util.List;

import microservice.cloud.inventory.shared.domain.event.DomainEvent;

public interface EventPublisher {

    public void publish(List<? extends DomainEvent> events);
}
