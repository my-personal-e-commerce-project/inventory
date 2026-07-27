package microservice.cloud.inventory.productStock.infrastructure.adapters;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import microservice.cloud.inventory.product.domain.entity.ProductRepository;
import microservice.cloud.inventory.productStock.application.use_cases.DecrementProductStockUseCase;
import microservice.cloud.inventory.productStock.application.use_cases.IncrementProductStockUseCase;
import microservice.cloud.inventory.productStock.domain.repository.ProductStockRepository;
import microservice.cloud.inventory.shared.application.ports.out.EventPublisher;

@Configuration
public class ProductStockConfigAdapter {

    @Bean
    public IncrementProductStockUseCase incrementProductStockUseCase(ProductStockRepository productStockRepository, ProductRepository productRepository, EventPublisher eventPublisher) {
        return new IncrementProductStockUseCase(productStockRepository, productRepository, eventPublisher);
    }

    @Bean
    public DecrementProductStockUseCase updateProductStockUseCase(ProductStockRepository productStockRepository, ProductRepository productRepository, EventPublisher eventPublisher) {
        return new DecrementProductStockUseCase(productStockRepository, productRepository, eventPublisher);
    }
}
