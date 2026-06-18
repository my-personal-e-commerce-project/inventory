package microservice.cloud.inventory.category.infrastructure.adapter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import microservice.cloud.inventory.category.domain.event.DeletedCategory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.function.Supplier;

@Configuration
public class CategoryEventProducerConfig {
    private final Sinks.Many<DeletedCategory> processor = Sinks.many().unicast().onBackpressureBuffer();

    @Bean
    public Supplier<Flux<DeletedCategory>> deletedCategoryProducer() {
        return () -> processor.asFlux();
    }

    public void sendMessage(DeletedCategory event) {
        processor.emitNext(event, Sinks.EmitFailureHandler.FAIL_FAST);
    }
}
