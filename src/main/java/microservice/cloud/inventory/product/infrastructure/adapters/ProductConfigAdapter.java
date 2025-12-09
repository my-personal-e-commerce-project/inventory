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
import microservice.cloud.inventory.shared.application.ports.in.GetMePort;
import microservice.cloud.inventory.shared.application.ports.out.EventPublishedPort;
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
        AttributeDefinitionRepository attributeDefinitionRepository,
        EventPublishedPort eventPublishedPort,
        GetMePort getMePort
    ) {
        return new CreateProductUseCase(
                productRepository, 
                attributeDefinitionRepository, 
                eventPublishedPort,
                getMePort
            );
    }

    @Bean
    public UpdateProductUseCasePort updateProductUseCasePort(
        ProductRepository productRepository,
        AttributeDefinitionRepository attributeDefinitionRepository,
        EventPublishedPort eventPublishedPort,
        GetMePort getMePort
    ) {
        return new UpdateProductUseCase(
                productRepository, 
                attributeDefinitionRepository, 
                eventPublishedPort,
                getMePort
            );
    }

    @Bean
    public DeleteProductUseCasePort deleteProductUseCasePort(
        ProductRepository productRepository,
        EventPublishedPort eventPublishedPort,
        GetMePort getMePort
    ) {
        return new DeleteProductUseCase(productRepository, eventPublishedPort, getMePort);
    }
    
    @Bean
    public AddProductAttributeUseCasePort addProductAttributeUseCasePort(
        ProductRepository productRepository,
        AttributeDefinitionRepository attributeDefinitionRepository,
        EventPublishedPort eventPublishedPort,
        GetMePort getMePort
    ) {
        return new AddProductAttributeUseCase(
                productRepository, 
                attributeDefinitionRepository, 
                eventPublishedPort,
                getMePort
            );
    }

    @Bean
    public DeleteProductAttributeUseCasePort deleteProductAttributeUseCasePort(
        ProductRepository productRepository,
        EventPublishedPort eventPublishedPort,
        GetMePort getMePort
    ) {
        return new DeleteProductAttributeUseCase(productRepository, eventPublishedPort, getMePort);
    }
}
