package microservice.cloud.inventory.product.infrastructure.adapters;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import microservice.cloud.inventory.product.domain.entity.ProductRepository;
import microservice.cloud.inventory.product.infrastructure.adapters.in.AddProductAttributeUseCaseDispatchEventDecorator;
import microservice.cloud.inventory.product.infrastructure.adapters.in.CreateProductUseCaseDispatchEventDecorator;
import microservice.cloud.inventory.product.infrastructure.adapters.in.DeleteProductAttributeUseCasePortDispatchEventDecorator;
import microservice.cloud.inventory.product.infrastructure.adapters.in.DeleteProductUseCaseDispatchEventDecorator;
import microservice.cloud.inventory.product.infrastructure.adapters.in.UpdateProductUseCaseDispatchEventDecorator;
import microservice.cloud.inventory.attribute.domain.repository.AttributeDefinitionRepository;
import microservice.cloud.inventory.category.domain.repository.CategoryRepository;
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
        CategoryRepository categoryRepository,
        GetMePort getMePort,
        EventPublishedPort eventPublishedPort
    ) {
        return new CreateProductUseCaseDispatchEventDecorator( 
                new CreateProductUseCase(
                    productRepository,
                    categoryRepository,
                    getMePort
                ),
                eventPublishedPort
            );
    }

    @Bean
    public UpdateProductUseCasePort updateProductUseCasePort(
        ProductRepository productRepository,
        CategoryRepository categoryRepository,
        GetMePort getMePort,
        EventPublishedPort eventPublishedPort
    ) {
        return new UpdateProductUseCaseDispatchEventDecorator( 
                new UpdateProductUseCase(
                    productRepository, 
                    categoryRepository, 
                    getMePort
                ),
                eventPublishedPort
            );
    }

    @Bean
    public DeleteProductUseCasePort deleteProductUseCasePort(
        ProductRepository productRepository,
        GetMePort getMePort,
        EventPublishedPort eventPublishedPort
    ) {
        return new DeleteProductUseCaseDispatchEventDecorator (
                new DeleteProductUseCase(productRepository, getMePort),
                eventPublishedPort
            );
    }
    
    @Bean
    public AddProductAttributeUseCasePort addProductAttributeUseCasePort(
        ProductRepository productRepository,
        AttributeDefinitionRepository attributeDefinitionRepository,
        CategoryRepository categoryRepository,
        EventPublishedPort eventPublishedPort,
        GetMePort getMePort
    ) {
        return new AddProductAttributeUseCaseDispatchEventDecorator(
                new AddProductAttributeUseCase(
                    productRepository,
                    attributeDefinitionRepository,
                    categoryRepository, 
                    getMePort
                ),
                eventPublishedPort
            );
    }

    @Bean
    public DeleteProductAttributeUseCasePort deleteProductAttributeUseCasePort(
        ProductRepository productRepository,
        GetMePort getMePort,
        CategoryRepository categoryRepository,
        EventPublishedPort eventPublishedPort
    ) {
        return new DeleteProductAttributeUseCasePortDispatchEventDecorator(
                new DeleteProductAttributeUseCase(
                    productRepository,
                    categoryRepository,
                    getMePort
                ), 
                eventPublishedPort
            );
    }
}
