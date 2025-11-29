package microservice.cloud.inventory.product.application.use_cases;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.persistence.EntityNotFoundException;
import microservice.cloud.inventory.attribute.domain.entity.AttributeDefinition;
import microservice.cloud.inventory.attribute.domain.repository.AttributeDefinitionRepository;
import microservice.cloud.inventory.product.application.ports.in.CreateProductUseCasePort;
import microservice.cloud.inventory.product.domain.entity.Product;
import microservice.cloud.inventory.product.domain.entity.ProductAttributeValue;
import microservice.cloud.inventory.product.domain.entity.ProductRepository;
import microservice.cloud.inventory.shared.domain.exception.DataNotFound;

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
        List<String> attrDefinitionIds = product.attributeValues().stream().map((ProductAttributeValue attr) -> attr.attribute_definition().value()).toList();

        Map<String, AttributeDefinition> attrs = attributeDefinitionRepository.getListByIds(
            attrDefinitionIds
        );

        product.attributeValues().stream().forEach(attrValue -> {
            AttributeDefinition attr = attrs.get(attrValue.attribute_definition().value());

            if(attr == null)
                throw new DataNotFound(
                        "Attribute definition not found: " 
                        + attrValue.attribute_definition().value()
                    );
 
            attrValue.validTypes(attr);
        });

        productRepository.save(product);
    }
}
