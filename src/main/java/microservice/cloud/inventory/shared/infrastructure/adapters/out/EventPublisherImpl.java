package microservice.cloud.inventory.shared.infrastructure.adapters.out;

import java.util.List;

import org.springframework.stereotype.Component;

import microservice.cloud.inventory.shared.application.ports.out.EventPublisher;
import microservice.cloud.inventory.shared.domain.event.DomainEvent;

@Component
public class EventPublisherImpl implements EventPublisher {

    @Override
    public void publish(List<? extends DomainEvent> events) {
        // TODO Auto-generated method stub
    }
}
