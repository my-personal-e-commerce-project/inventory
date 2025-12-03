package microservice.cloud.inventory.product.application.use_cases;

import java.util.ArrayList;
import java.util.List;

import microservice.cloud.inventory.attribute.domain.entity.AttributeDefinition;
import microservice.cloud.inventory.attribute.domain.repository.AttributeDefinitionRepository;
import microservice.cloud.inventory.product.application.ports.in.AddProductAttributeUseCasePort;
import microservice.cloud.inventory.product.domain.entity.Product;
import microservice.cloud.inventory.product.domain.entity.ProductAttributeValue;
import microservice.cloud.inventory.product.domain.entity.ProductRepository;
import microservice.cloud.inventory.shared.domain.value_objects.Id;

public class AddProductAttributeUseCase implements AddProductAttributeUseCasePort {

    private ProductRepository productRepository;
    private AttributeDefinitionRepository attributeDefinitionRepository;

    public AddProductAttributeUseCase(
        ProductRepository productRepository,
        AttributeDefinitionRepository attributeDefinitionRepository
    ) {
        this.productRepository = productRepository;
        this.attributeDefinitionRepository = attributeDefinitionRepository;
    }

    @Override
    public void execute(Id productId, ProductAttributeValue productAttributeValue) {
        Product product = productRepository.findById(productId);

        AttributeDefinition attributeDefinition = new ArrayList<>(
            attributeDefinitionRepository
                .getListByIds(
                    List.of(
                        productAttributeValue.attribute_definition().value()
                    )
                ).values()
            ).getFirst();

        if(attributeDefinition == null)
            throw new RuntimeException(
                    "Attribute definition not found: " 
                    + productAttributeValue.attribute_definition().value()
                );

        productAttributeValue.validTypes(attributeDefinition);

        product.addProductAttribute(productAttributeValue);

        productRepository.addProductAttribute(productId, productAttributeValue);
    } 
}
