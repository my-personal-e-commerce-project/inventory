package microservice.cloud.inventory.shared.infrastructure.consumer;

import java.util.function.Consumer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

import lombok.RequiredArgsConstructor;
import microservice.cloud.inventory.category.application.use_cases.EnabledCategoryUseCase;
import microservice.cloud.inventory.category.application.use_cases.RealDeleteCategoryUseCase;
import microservice.cloud.inventory.shared.domain.value_objects.Id;
import microservice.cloud.inventory.shared.infrastructure.dto.CategoryDiscountRemoved;

@RequiredArgsConstructor
@Configuration
public class KafkaConsumer {
   
    private final RealDeleteCategoryUseCase realDeleteCategoryUseCase;
    private final EnabledCategoryUseCase enabledCategoryUseCase;

    @Bean
    public Consumer<Message<CategoryDiscountRemoved>> discountSagaHandler() {
        return message -> {
           
            if(message.getPayload().success()) {
                realDeleteCategoryUseCase.execute(Id.fromString(message.getPayload().categoryId()));
            } else {
                enabledCategoryUseCase.execute(Id.fromString(message.getPayload().categoryId()));
            }
        };
    }
}
