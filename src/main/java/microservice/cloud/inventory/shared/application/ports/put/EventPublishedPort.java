package microservice.cloud.inventory.shared.application.ports.put;

import java.util.List;

import microservice.cloud.inventory.shared.domain.event.DomainEvent;

public interface EventPublishedPort {

    public void publish(List<DomainEvent> event);
}
