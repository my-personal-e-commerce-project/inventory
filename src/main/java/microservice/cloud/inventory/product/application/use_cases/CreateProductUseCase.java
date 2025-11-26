package microservice.cloud.inventory.product.application.use_cases;

import java.util.Map;

import microservice.cloud.inventory.attribute.domain.entity.AttributeDefinition;
import microservice.cloud.inventory.attribute.domain.repository.AttributeDefinitionRepository;
import microservice.cloud.inventory.product.application.ports.in.CreateProductUseCasePort;
import microservice.cloud.inventory.product.domain.entity.Product;
import microservice.cloud.inventory.product.domain.entity.ProductAttributeValue;
import microservice.cloud.inventory.product.domain.entity.ProductRepository;

public class CreateProductUseCase implements CreateProductUseCasePort {

    private ProductRepository productRepository;
    private AttributeDefinitionRepository attributeDefinitionRepository;

    public CreateProductUseCase(
        ProductRepository productRepository,
        AttributeDefinitionRepository attributeDefinitionRepository
    ) {


        this.productRepository = productRepository;
        this.attributeDefinitionRepository = attributeDefinitionRepository;
    }

    public void execute(Product product) {
        Map<String, AttributeDefinition> attrs = attributeDefinitionRepository.getListByIds(
            product.attributeValues().stream().map((ProductAttributeValue attr) -> attr.attribute_definition().value()).toList()
        );

        product.attributeValues().stream().forEach(attrValue -> {
            AttributeDefinition attr = attrs.get(attrValue.attribute_definition().value());

            attrValue.validTypes(attr);
        });

        productRepository.save(product);
    }
}
