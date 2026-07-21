package microservice.cloud.inventory.productStock.infrastructure.adapters;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import microservice.cloud.inventory.product.domain.entity.ProductRepository;
import microservice.cloud.inventory.productStock.application.use_cases.CreateProductStockUseCase;
import microservice.cloud.inventory.productStock.application.use_cases.DecrementProductStockUseCase;
import microservice.cloud.inventory.productStock.application.use_cases.UpdateProductStockUseCase;
import microservice.cloud.inventory.productStock.domain.repository.ProductStockRepository;
import microservice.cloud.inventory.shared.application.ports.out.EventPublisher;

@Configuration
public class ProductStockConfigAdapter {

    @Bean
    public DecrementProductStockUseCase decrementProductStockUseCase(ProductStockRepository productStockRepository, ProductRepository productRepository, EventPublisher eventPublisher) {
        return new DecrementProductStockUseCase(productStockRepository, productRepository, eventPublisher);
    }

    @Bean
    public UpdateProductStockUseCase updateProductStockUseCase(ProductStockRepository productStockRepository, ProductRepository productRepository, EventPublisher eventPublisher) {
        return new UpdateProductStockUseCase(productRepository, productStockRepository, eventPublisher);
    }

    @Bean
    public CreateProductStockUseCase createProductStockUseCase(ProductStockRepository productStockRepository) {
        return new CreateProductStockUseCase(productStockRepository);
    }
}
