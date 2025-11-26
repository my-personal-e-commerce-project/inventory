package microservice.cloud.inventory.product.infrastructure.adapters;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import microservice.cloud.inventory.attribute.domain.repository.AttributeDefinitionRepository;
import microservice.cloud.inventory.product.application.ports.in.CreateProductUseCasePort;
import microservice.cloud.inventory.product.application.ports.in.ListProductsUseCasePort;
import microservice.cloud.inventory.product.application.ports.put.ProductReadRepository;
import microservice.cloud.inventory.product.application.use_cases.CreateProductUseCase;
import microservice.cloud.inventory.product.application.use_cases.ListProductsUseCase;
import microservice.cloud.inventory.product.domain.entity.ProductRepository;

@Configuration
public class ProductConfigAdapter {

    @Bean
    public ListProductsUseCasePort listProductsUseCasePort(
        ProductReadRepository productReadRepository
    ) {
        return new ListProductsUseCase(productReadRepository);
    }

    @Bean
    public CreateProductUseCasePort CreateProductUseCasePort(
        ProductRepository productRepository,
        AttributeDefinitionRepository attributeDefinitionRepository
    ) {
        return new CreateProductUseCase(productRepository, attributeDefinitionRepository);
    }
}
