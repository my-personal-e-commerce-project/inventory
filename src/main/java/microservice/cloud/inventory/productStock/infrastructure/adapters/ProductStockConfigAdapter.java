package microservice.cloud.inventory.productStock.infrastructure.adapters;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import microservice.cloud.inventory.productStock.application.use_cases.CreateProductStockUseCase;
import microservice.cloud.inventory.productStock.application.use_cases.DecrementProductStockUseCase;
import microservice.cloud.inventory.productStock.application.use_cases.UpdateProductStockUseCase;
import microservice.cloud.inventory.productStock.domain.repository.ProductStockRepository;

@Configuration
public class ProductStockConfigAdapter {

    @Bean
    public DecrementProductStockUseCase decrementProductStockUseCase(ProductStockRepository productStockRepository) {
        return new DecrementProductStockUseCase(productStockRepository);
    }

    @Bean
    public UpdateProductStockUseCase updateProductStockUseCase(ProductStockRepository productStockRepository) {
        return new UpdateProductStockUseCase(productStockRepository);
    }

    @Bean
    public CreateProductStockUseCase createProductStockUseCase(ProductStockRepository productStockRepository) {
        return new CreateProductStockUseCase(productStockRepository);
    }
}
