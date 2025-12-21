package microservice.cloud.inventory.product.application.use_cases;

import java.util.ArrayList;
import java.util.List;

import microservice.cloud.inventory.attribute.domain.entity.AttributeDefinition;
import microservice.cloud.inventory.attribute.domain.repository.AttributeDefinitionRepository;
import microservice.cloud.inventory.category.domain.entity.CategoryAttribute;
import microservice.cloud.inventory.category.domain.repository.CategoryRepository;
import microservice.cloud.inventory.product.application.ports.in.AddProductAttributeUseCasePort;
import microservice.cloud.inventory.shared.application.ports.in.GetMePort;
import microservice.cloud.inventory.product.domain.entity.Product;
import microservice.cloud.inventory.product.domain.entity.ProductAttributeValue;
import microservice.cloud.inventory.product.domain.entity.ProductRepository;
import microservice.cloud.inventory.shared.domain.exception.DataNotFound;
import microservice.cloud.inventory.shared.domain.value_objects.Id;

public class AddProductAttributeUseCase implements AddProductAttributeUseCasePort {

    private ProductRepository productRepository;
    private AttributeDefinitionRepository attributeDefinitionRepository;
    private GetMePort getMePort;
    private CategoryRepository categoryRepository;

    public AddProductAttributeUseCase(
        ProductRepository productRepository,
        AttributeDefinitionRepository attributeDefinitionRepository,
        CategoryRepository categoryRepository,
        GetMePort getMePort
    ) {
        this.productRepository = productRepository;
        this.attributeDefinitionRepository = attributeDefinitionRepository;
        this.getMePort = getMePort;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Product execute(Id productId, ProductAttributeValue productAttributeValue) {
        Product product = productRepository.findById(productId);

        AttributeDefinition attributeDefinition = attributeDefinitionRepository
            .getById(productAttributeValue.id());

        if(attributeDefinition == null)
            throw new DataNotFound(
                    "Attribute definition not found: " 
                    + productAttributeValue.attribute_definition().value()
                );

        product.addProductAttribute(getMePort.execute(), productAttributeValue);

        List<CategoryAttribute> attrs = 
           categoryRepository 
            .getCategoryAttributesByCategoryIds(
                product.categories()
            );

        product.validAttributes(new ArrayList<>(attrs));
        
        productRepository.update(product);
        
        return product;
    } 
}
