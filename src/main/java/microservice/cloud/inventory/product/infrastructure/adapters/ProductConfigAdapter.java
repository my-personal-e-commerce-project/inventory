package microservice.cloud.inventory.product.infrastructure.adapters;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import microservice.cloud.inventory.product.domain.entity.ProductRepository;
import microservice.cloud.inventory.attribute.domain.repository.AttributeDefinitionRepository;

import microservice.cloud.inventory.product.application.ports.in.AddProductAttributeUseCasePort;
import microservice.cloud.inventory.product.application.ports.in.CreateProductUseCasePort;
import microservice.cloud.inventory.product.application.ports.in.DeleteProductAttributeUseCasePort;
import microservice.cloud.inventory.product.application.ports.in.DeleteProductUseCasePort;
import microservice.cloud.inventory.product.application.ports.in.ListProductsUseCasePort;
import microservice.cloud.inventory.product.application.ports.in.UpdateProductUseCasePort;
import microservice.cloud.inventory.product.application.ports.put.ProductReadRepository;

import microservice.cloud.inventory.product.application.use_cases.AddProductAttributeUseCase;
import microservice.cloud.inventory.product.application.use_cases.CreateProductUseCase;
import microservice.cloud.inventory.product.application.use_cases.DeleteProductAttributeUseCase;
import microservice.cloud.inventory.product.application.use_cases.DeleteProductUseCase;
import microservice.cloud.inventory.product.application.use_cases.ListProductsUseCase;
import microservice.cloud.inventory.product.application.use_cases.UpdateProductUseCase;


@Configuration
public class ProductConfigAdapter {

    @Bean
    public ListProductsUseCasePort listProductsUseCasePort(
        ProductReadRepository productReadRepository
    ) {
        return new ListProductsUseCase(productReadRepository);
    }

    @Bean
    public CreateProductUseCasePort createProductUseCasePort(
        ProductRepository productRepository,
        AttributeDefinitionRepository attributeDefinitionRepository
    ) {
        return new CreateProductUseCase(productRepository, attributeDefinitionRepository);
    }

    @Bean
    public UpdateProductUseCasePort updateProductUseCasePort(
            ProductRepository productRepository
    ) {
        return new UpdateProductUseCase(productRepository);
    }

    @Bean
    public DeleteProductUseCasePort deleteProductUseCasePort(
        ProductRepository productRepository
    ) {
        return new DeleteProductUseCase(productRepository);
    }
    
    @Bean
    public AddProductAttributeUseCasePort addProductAttributeUseCasePort(
        ProductRepository productRepository
    ) {
        return new AddProductAttributeUseCase(productRepository);
    }

    @Bean
    public DeleteProductAttributeUseCasePort deleteProductAttributeUseCasePort(
        ProductRepository productRepository
    ) {
        return new DeleteProductAttributeUseCase(productRepository);
    }
}
