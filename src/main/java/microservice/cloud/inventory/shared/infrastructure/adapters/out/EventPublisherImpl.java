package microservice.cloud.inventory.shared.infrastructure.adapters.out;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import microservice.cloud.inventory.category.domain.event.DeletedCategory;
import microservice.cloud.inventory.product.domain.event.MinStockAlertEvent;
import microservice.cloud.inventory.shared.application.ports.out.DomainOutboxDaoInterface;
import microservice.cloud.inventory.shared.application.ports.out.EventPublisher;
import microservice.cloud.inventory.shared.domain.event.DomainEvent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Slf4j
@RequiredArgsConstructor
@Component
public class EventPublisherImpl implements EventPublisher {

    private final ObjectMapper objectMapper;
    private final DomainOutboxDaoInterface domainOutboxDao;

    @Transactional
    @Override
    public void publish(List<? extends DomainEvent> events) {
        if (events == null || events.isEmpty()) return;
  
        try {
            for (DomainEvent e : events) {
                if(e instanceof MinStockAlertEvent) {
                    handleMinStockAlert((MinStockAlertEvent) e);

                }
                if(e instanceof DeletedCategory) {
                    handleDeletedCategory((DeletedCategory) e);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("Serializer error:" + e.getMessage());
        }
    }

    public void handleMinStockAlert(MinStockAlertEvent event) throws JsonProcessingException {
        String payload = objectMapper.writeValueAsString(event);
        domainOutboxDao.save(event.topic(), payload);
    } 

    public void handleDeletedCategory(DeletedCategory event) throws JsonProcessingException {
        String payload = objectMapper.writeValueAsString(event);
        domainOutboxDao.save(event.topic(), payload);
    }
}
