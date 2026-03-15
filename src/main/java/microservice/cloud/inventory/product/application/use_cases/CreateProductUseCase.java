package microservice.cloud.inventory.product.application.use_cases;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import microservice.cloud.inventory.attribute.domain.entity.AttributeDefinition;
import microservice.cloud.inventory.attribute.domain.repository.AttributeDefinitionRepository;
import microservice.cloud.inventory.category.domain.entity.CategoryAttribute;
import microservice.cloud.inventory.category.domain.repository.CategoryRepository;
import microservice.cloud.inventory.product.application.ports.in.CreateProductUseCasePort;
import microservice.cloud.inventory.shared.application.ports.out.GetMePort;
import microservice.cloud.inventory.product.domain.entity.Product;
import microservice.cloud.inventory.product.domain.entity.ProductRepository;

public class CreateProductUseCase implements CreateProductUseCasePort {

    private ProductRepository productRepository;
    private CategoryRepository categoryRepository;
    private AttributeDefinitionRepository attributeDefinitionRepository;
    private GetMePort getMePort;

    public CreateProductUseCase(
        ProductRepository productRepository,
        CategoryRepository categoryRepository,
        AttributeDefinitionRepository attributeDefinitionRepository,
        GetMePort getMePort
    ) {

        this.attributeDefinitionRepository = attributeDefinitionRepository;
        this.getMePort = getMePort;
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }
   
    @Override
    public Product execute(
        Product product
    ) {
        categoryRepository.isValidTheseCategoryIds(product.categories());

        List<AttributeDefinition> default_attributes = attributeDefinitionRepository
            .getGlobalAttributes();

        Set<CategoryAttribute> attrs = 
           categoryRepository 
            .getCategoryAttributesWithAttributeDefinitionsByCategoryIds(
                product.categories()
            );

        if(attrs != null)
            product.validAttributes(attrs);

        if(default_attributes != null)
            product.validDefaultAttributes(default_attributes);

        product.create(getMePort.execute());
        
        productRepository.save(product);

        return product;
    }
}
