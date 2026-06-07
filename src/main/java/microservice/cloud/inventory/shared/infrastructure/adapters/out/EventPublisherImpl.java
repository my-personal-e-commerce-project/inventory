package microservice.cloud.inventory.shared.infrastructure.adapters.out;

import java.util.List;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import microservice.cloud.inventory.category.domain.event.DeletedCategory;
import microservice.cloud.inventory.shared.application.ports.out.EventPublisher;
import microservice.cloud.inventory.shared.domain.event.DomainEvent;

@Slf4j
@RequiredArgsConstructor
@Component
public class EventPublisherImpl implements EventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void publish(List<? extends DomainEvent> events) {
        if (events == null || events.isEmpty()) return;
   
        events.forEach(e -> {
            if(e instanceof DeletedCategory) {
                handleDeletedCategory((DeletedCategory) e);
            }
        });
    }

    public void handleDeletedCategory(DeletedCategory event) throws RuntimeException {
        try {
            String messageKey = String.valueOf(event.aggregateId()); 
            
            kafkaTemplate.send("inventory.category.saga-events", messageKey, event).get();
        } catch (Exception e) {
            log.error(
                "CRITICAL: DeletedCategory event could not be sent via Kafka: " + e.getMessage()
            );
        }
    }
}
