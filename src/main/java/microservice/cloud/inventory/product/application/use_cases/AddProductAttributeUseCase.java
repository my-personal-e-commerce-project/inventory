package microservice.cloud.inventory.product.application.use_cases;

import microservice.cloud.inventory.attribute.domain.entity.AttributeDefinition;
import microservice.cloud.inventory.attribute.domain.repository.AttributeDefinitionRepository;
import microservice.cloud.inventory.product.application.ports.in.AddProductAttributeUseCasePort;
import microservice.cloud.inventory.shared.application.ports.put.EventPublishedPort;
import microservice.cloud.inventory.product.domain.entity.Product;
import microservice.cloud.inventory.product.domain.entity.ProductAttributeValue;
import microservice.cloud.inventory.product.domain.entity.ProductRepository;
import microservice.cloud.inventory.shared.domain.value_objects.Id;

public class AddProductAttributeUseCase implements AddProductAttributeUseCasePort {

    private ProductRepository productRepository;
    private AttributeDefinitionRepository attributeDefinitionRepository;
    private EventPublishedPort eventPublishedPort;

    public AddProductAttributeUseCase(
        ProductRepository productRepository,
        AttributeDefinitionRepository attributeDefinitionRepository,
        EventPublishedPort eventPublishedPort
    ) {
        this.productRepository = productRepository;
        this.attributeDefinitionRepository = attributeDefinitionRepository;
        this.eventPublishedPort = eventPublishedPort;
    }

    @Override
    public void execute(Id productId, ProductAttributeValue productAttributeValue) {
        Product product = productRepository.findById(productId);

        AttributeDefinition attributeDefinition = attributeDefinitionRepository
            .getById(productAttributeValue.id());

        if(attributeDefinition == null)
            throw new RuntimeException(
                    "Attribute definition not found: " 
                    + productAttributeValue.attribute_definition().value()
                );

        productAttributeValue.validTypes(attributeDefinition);

        product.addProductAttribute(productAttributeValue);

        productRepository.addProductAttribute(productId, productAttributeValue);

        eventPublishedPort.publish(product.events());
    } 
}
