package microservice.cloud.inventory.product.application.use_cases;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import microservice.cloud.inventory.attribute.domain.entity.AttributeDefinition;
import microservice.cloud.inventory.attribute.domain.repository.AttributeDefinitionRepository;
import microservice.cloud.inventory.product.application.ports.in.UpdateProductUseCasePort;
import microservice.cloud.inventory.product.domain.entity.Product;
import microservice.cloud.inventory.product.domain.entity.ProductRepository;
import microservice.cloud.inventory.shared.domain.value_objects.Id;

public class UpdateProductUseCase implements UpdateProductUseCasePort {

    private ProductRepository productRepository;
    private AttributeDefinitionRepository attributeDefinitionRepository;

    public UpdateProductUseCase(
        ProductRepository productRepository,
        AttributeDefinitionRepository attributeDefinitionRepository
    ) {
        this.productRepository = productRepository;
        this.attributeDefinitionRepository = attributeDefinitionRepository;
    }

    @Override
    public void execute(Product product) {
        Product p = productRepository.findById(new Id(product.id().value()));
      
        List<String> categories = product
            .categories();

        List<AttributeDefinition> attrs = 
            attributeDefinitionRepository
            .getByCategoryAttributeIds(
                categories
            );

        product.validAttributes(new ArrayList<>(attrs));

        p.update(product);
        
        productRepository.update(p);
    }
}
