package microservice.cloud.inventory.shared.application.ports.out;

import java.util.List;

import microservice.cloud.inventory.shared.infrastructure.event.BaseEvent;

public interface EventPublishedPort {

    public void publish(List<BaseEvent> event);
}
