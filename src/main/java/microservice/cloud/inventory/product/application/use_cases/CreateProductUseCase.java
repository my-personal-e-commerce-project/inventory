package microservice.cloud.inventory.product.application.use_cases;

import java.util.ArrayList;
import java.util.List;

import microservice.cloud.inventory.attribute.domain.entity.AttributeDefinition;
import microservice.cloud.inventory.attribute.domain.repository.AttributeDefinitionRepository;
import microservice.cloud.inventory.product.application.ports.in.CreateProductUseCasePort;
import microservice.cloud.inventory.shared.application.ports.put.EventPublishedPort;
import microservice.cloud.inventory.product.domain.entity.Product;
import microservice.cloud.inventory.product.domain.entity.ProductRepository;

public class CreateProductUseCase implements CreateProductUseCasePort {

    private ProductRepository productRepository;
    private AttributeDefinitionRepository attributeDefinitionRepository;
    private EventPublishedPort eventPublishedPort;

    public CreateProductUseCase(
        ProductRepository productRepository,
        AttributeDefinitionRepository attributeDefinitionRepository,
        EventPublishedPort eventPublishedPort
    ) {

        this.productRepository = productRepository;
        this.attributeDefinitionRepository = attributeDefinitionRepository;
        this.eventPublishedPort = eventPublishedPort;
    }

    public void execute(Product product) {
        List<String> categories = product
            .categories();

        List<AttributeDefinition> attrs = 
            attributeDefinitionRepository
            .getByCategoryAttributeIds(
                categories
            );

        product.validAttributes(new ArrayList<>(attrs));

        productRepository.save(product);

        eventPublishedPort.publish(product.events());
    }
}
