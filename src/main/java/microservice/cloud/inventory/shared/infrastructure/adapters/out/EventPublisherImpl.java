package microservice.cloud.inventory.shared.infrastructure.adapters.out;

import java.util.List;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import microservice.cloud.inventory.category.domain.event.DeletedCategory;
import microservice.cloud.inventory.category.infrastructure.adapter.CategoryEventProducerConfig;
import microservice.cloud.inventory.product.domain.event.MinStockAlertEvent;
import microservice.cloud.inventory.product.infrastructure.adapters.MinStockAlertProducerConfig;
import microservice.cloud.inventory.shared.application.ports.out.EventPublisher;
import microservice.cloud.inventory.shared.domain.event.DomainEvent;

@Slf4j
@RequiredArgsConstructor
@Component
public class EventPublisherImpl implements EventPublisher {

    private final CategoryEventProducerConfig categoryEventProducerConfig;
    private final MinStockAlertProducerConfig minStockAlertProducerConfig;

    @Override
    public void publish(List<? extends DomainEvent> events) {
        if (events == null || events.isEmpty()) return;
   
        events.forEach(e -> {
            if(e instanceof DeletedCategory) {
                handleDeletedCategory((DeletedCategory) e);
            }
        });
    }

    public void handleMinStockAlert(MinStockAlertEvent event) {
        try {
            minStockAlertProducerConfig.sendMessage(event);
        } catch (Exception e) {
            log.error(
                "CRITICAL: MinStockAlertEvent event could not be sent via Kafka: " + e.getMessage()
            );
        }
    } 

    public void handleDeletedCategory(DeletedCategory event) {
        try {
            categoryEventProducerConfig.sendMessage(event);
        } catch (Exception e) {
            log.error(
                "CRITICAL: DeletedCategory event could not be sent via Kafka: " + e.getMessage()
            );
        }
    }
}
