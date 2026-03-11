package microservice.cloud.inventory.product.application.use_cases;

import java.util.ArrayList;
import java.util.List;

import microservice.cloud.inventory.attribute.domain.entity.AttributeDefinition;
import microservice.cloud.inventory.attribute.domain.repository.AttributeDefinitionRepository;
import microservice.cloud.inventory.category.domain.entity.CategoryAttribute;
import microservice.cloud.inventory.category.domain.repository.CategoryRepository;
import microservice.cloud.inventory.product.application.ports.in.AddProductAttributeUseCasePort;
import microservice.cloud.inventory.shared.application.ports.out.GetMePort;
import microservice.cloud.inventory.product.domain.entity.Product;
import microservice.cloud.inventory.product.domain.entity.ProductAttributeValue;
import microservice.cloud.inventory.product.domain.entity.ProductRepository;
import microservice.cloud.inventory.shared.domain.value_objects.Id;

public class AddProductAttributeUseCase implements AddProductAttributeUseCasePort {

    private ProductRepository productRepository;
    private GetMePort getMePort;

    public AddProductAttributeUseCase(
        ProductRepository productRepository,
        GetMePort getMePort
    ) {
        this.productRepository = productRepository;
        this.getMePort = getMePort;
    }

    @Override
    public Product execute(Id productId, ProductAttributeValue productAttributeValue) {
        Product product = productRepository.findById(productId);
        
        product.addProductAttribute(getMePort.execute(), productAttributeValue);
        
        productRepository.update(product);
        
        return product;
    } 
}
