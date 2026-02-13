package microservice.cloud.inventory.product.application.use_cases;

import java.util.ArrayList;
import java.util.List;

import microservice.cloud.inventory.attribute.domain.entity.AttributeDefinition;
import microservice.cloud.inventory.attribute.domain.repository.AttributeDefinitionRepository;
import microservice.cloud.inventory.category.domain.entity.CategoryAttribute;
import microservice.cloud.inventory.category.domain.repository.CategoryRepository;
import microservice.cloud.inventory.product.application.ports.in.CreateProductUseCasePort;
import microservice.cloud.inventory.shared.application.ports.in.GetMePort;
import microservice.cloud.inventory.shared.domain.value_objects.Id;
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
        Id id,
        String title, 
        Slug slug, 
        String description,
        List<String> categories,
        Price price,
        Quantity stock,
        List<String> images,
        List<ProductAttributeValue> attributes,
        List<String> tags
    ) {
        List<AttributeDefinition> default_attributes = attributeDefinitionRepository
            .getGlobalAttributes();

        List<CategoryAttribute> attrs = 
           categoryRepository 
            .getCategoryAttributesByCategoryIds(
                categories
            );

        Product newProduct = Product.factory(
            getMePort.execute(),
            id,
            title, 
            slug, 
            description, 
            categories, 
            price, 
            stock, 
            images, 
            attributes,
            tags
        );

        newProduct.validAttributes(new ArrayList<>(attrs));

        newProduct.validDefaultAttributes(default_attributes);

        productRepository.save(newProduct);

        return newProduct;
    }
}
