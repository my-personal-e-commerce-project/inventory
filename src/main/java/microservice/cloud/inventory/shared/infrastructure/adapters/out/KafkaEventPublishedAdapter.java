package microservice.cloud.inventory.shared.infrastructure.adapters.out;

import java.util.List;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import microservice.cloud.inventory.shared.application.ports.out.EventPublishedPort;
import microservice.cloud.inventory.shared.domain.event.DomainEvent;

@Component
public class KafkaEventPublishedAdapter implements EventPublishedPort {

    private KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaEventPublishedAdapter(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publish(List<DomainEvent> events) {
        events.stream().forEach(e -> {
            kafkaTemplate.send(e.eventName(), e.aggregateId(), e);
        });
    }
}
