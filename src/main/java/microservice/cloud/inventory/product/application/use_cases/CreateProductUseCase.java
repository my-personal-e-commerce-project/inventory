package microservice.cloud.inventory.product.application.use_cases;

import java.util.HashSet;
import java.util.List;

import microservice.cloud.inventory.attribute.domain.entity.AttributeDefinition;
import microservice.cloud.inventory.attribute.domain.repository.AttributeDefinitionRepository;
import microservice.cloud.inventory.category.domain.entity.CategoryAttribute;
import microservice.cloud.inventory.category.domain.repository.CategoryRepository;
import microservice.cloud.inventory.product.application.ports.in.CreateProductUseCasePort;
import microservice.cloud.inventory.shared.application.ports.out.GetMePort;
import microservice.cloud.inventory.shared.domain.value_objects.Me;
import microservice.cloud.inventory.shared.domain.value_objects.Permission;
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
    public void execute(
        Product product
    ) {
        Me me = getMePort.execute();

        if(me == null)
            throw new RuntimeException("You do not have permission to perform this action");

        me.IHavePermission(Permission.createProduct());
        
        List<AttributeDefinition> defaultAttributes = attributeDefinitionRepository
            .getGlobalAttributes();

        List<CategoryAttribute> catAttrs = 
           categoryRepository 
            .getCategoryAttributesWithAttributeDefinitionsByCategoryIds(
                product.categories()
            );
        
        product.validGlobalAttributesAndCategoryAttributes(new HashSet<>(defaultAttributes), new HashSet<>(catAttrs));
      
        productRepository.save(product);
    }
}
