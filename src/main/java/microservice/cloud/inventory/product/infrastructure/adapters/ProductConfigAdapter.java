package microservice.cloud.inventory.product.infrastructure.adapters;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import microservice.cloud.inventory.product.domain.entity.ProductRepository;
import microservice.cloud.inventory.attribute.domain.repository.AttributeDefinitionRepository;
import microservice.cloud.inventory.category.domain.repository.CategoryRepository;
import microservice.cloud.inventory.product.application.use_cases.AddProductAttributeUseCase;
import microservice.cloud.inventory.product.application.use_cases.CreateProductUseCase;
import microservice.cloud.inventory.product.application.use_cases.DeleteProductAttributeUseCase;
import microservice.cloud.inventory.product.application.use_cases.DeleteProductUseCase;
import microservice.cloud.inventory.product.application.use_cases.ListProductsUseCase;
import microservice.cloud.inventory.product.application.use_cases.UpdateProductUseCase;
import microservice.cloud.inventory.shared.application.ports.out.EventPublisher;
import microservice.cloud.inventory.shared.application.ports.out.GetMePort;
import microservice.cloud.inventory.product.application.ports.out.ProductReadRepository;

@Configuration
public class ProductConfigAdapter {

    @Bean
    public ListProductsUseCase listProductsUseCase(
        ProductReadRepository productReadRepository
    ) {
        return new ListProductsUseCase(productReadRepository);
    }

    @Bean
    public CreateProductUseCase createProductUseCase(
        ProductRepository productRepository,
        CategoryRepository categoryRepository,
        AttributeDefinitionRepository attributeDefinitionRepository,
        EventPublisher eventPublisher,
        GetMePort getMe
    ) {
        return new CreateProductUseCase(
            productRepository,
            categoryRepository,
            attributeDefinitionRepository,
            eventPublisher,
            getMe
        );
    }

    @Bean
    public UpdateProductUseCase updateProductUseCase(
        ProductRepository productRepository,
        EventPublisher eventPublisher,
        GetMePort getMe
    ) {
        return new UpdateProductUseCase(
            productRepository,
            eventPublisher,
            getMe
        );
    }

    @Bean
    public DeleteProductUseCase deleteProductUseCase(
        ProductRepository productRepository,
        GetMePort getMe
    ) {
        return new DeleteProductUseCase(productRepository, getMe);
    }
    
    @Bean
    public AddProductAttributeUseCase addProductAttributeUseCase(
        ProductRepository productRepository,
        GetMePort getMe
    ) {
        return new AddProductAttributeUseCase(
            productRepository,
            getMe
        );
    }

    @Bean
    public DeleteProductAttributeUseCase deleteProductAttributeUseCase(
        ProductRepository productRepository,
        CategoryRepository categoryRepository,
        GetMePort getMe
    ) {
        return new DeleteProductAttributeUseCase(
            productRepository,
            categoryRepository,
            getMe
        );
    }
}
