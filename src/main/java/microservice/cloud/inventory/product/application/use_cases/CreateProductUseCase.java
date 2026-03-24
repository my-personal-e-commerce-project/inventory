package microservice.cloud.inventory.product.application.use_cases;

import java.util.List;
import java.util.Set;

import microservice.cloud.inventory.attribute.domain.entity.AttributeDefinition;
import microservice.cloud.inventory.attribute.domain.repository.AttributeDefinitionRepository;
import microservice.cloud.inventory.category.domain.entity.CategoryAttribute;
import microservice.cloud.inventory.category.domain.repository.CategoryRepository;
import microservice.cloud.inventory.product.application.ports.in.CreateProductUseCasePort;
import microservice.cloud.inventory.shared.application.ports.out.GetMePort;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;
import microservice.cloud.inventory.product.domain.entity.Product;
import microservice.cloud.inventory.product.domain.entity.ProductAttributeValue;
import microservice.cloud.inventory.product.domain.entity.ProductRepository;
import microservice.cloud.inventory.product.domain.value_objects.Price;
import microservice.cloud.inventory.product.domain.value_objects.Quantity;

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
        String title,
        Slug slug,
        String description,
        Set<String> categories, 
        Price price, 
        Set<ProductAttributeValue> attributeValues,
        Quantity stock,
        Set<String> images,
        Set<String> tags    
    ) {
        categoryRepository.isValidTheseCategoryIds(categories);

        List<AttributeDefinition> default_attributes = attributeDefinitionRepository
            .getGlobalAttributes();

        Set<CategoryAttribute> attrs = 
           categoryRepository 
            .getCategoryAttributesWithAttributeDefinitionsByCategoryIds(
                categories
            );

        Product product = Product.factory(
            getMePort.execute(), 
            title, 
            slug, 
            description, 
            categories, 
            price, 
            attributeValues, 
            stock, 
            images, 
            tags
        );

        if(attrs != null)
            product.validCategoryAttributes(attrs);

        if(default_attributes != null)
            product.validGlobalAttributeDefinitions(default_attributes);
        
        productRepository.save(product);

        return product;
    }
}
