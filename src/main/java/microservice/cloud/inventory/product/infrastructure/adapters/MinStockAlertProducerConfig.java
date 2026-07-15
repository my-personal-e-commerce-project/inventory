package microservice.cloud.inventory.product.infrastructure.adapters;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import microservice.cloud.inventory.product.domain.event.MinStockAlertEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.function.Supplier;

@Configuration
public class MinStockAlertProducerConfig {
    private final Sinks.Many<MinStockAlertEvent> processor = Sinks.many().unicast().onBackpressureBuffer();

    @Bean
    public Supplier<Flux<MinStockAlertEvent>> minStockAlertProducer() {
        return () -> processor.asFlux();
    }

    public void sendMessage(MinStockAlertEvent event) {
        processor.emitNext(event, Sinks.EmitFailureHandler.FAIL_FAST);
    }
 }
