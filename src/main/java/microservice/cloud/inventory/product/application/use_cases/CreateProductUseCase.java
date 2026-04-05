package microservice.cloud.inventory.product.application.use_cases;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import microservice.cloud.inventory.attribute.domain.entity.AttributeDefinition;
import microservice.cloud.inventory.attribute.domain.repository.AttributeDefinitionRepository;
import microservice.cloud.inventory.category.domain.entity.CategoryAttribute;
import microservice.cloud.inventory.category.domain.repository.CategoryRepository;
import microservice.cloud.inventory.coupon.domain.entity.Coupon;
import microservice.cloud.inventory.coupon.domain.repository.CouponRepository;
import microservice.cloud.inventory.product.application.ports.in.CreateProductUseCasePort;
import microservice.cloud.inventory.shared.application.ports.out.GetMePort;
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
    private CouponRepository couponRepository;
    private GetMePort getMePort;

    public CreateProductUseCase(
        ProductRepository productRepository,
        CategoryRepository categoryRepository,
        AttributeDefinitionRepository attributeDefinitionRepository,
        CouponRepository couponRepository,
        GetMePort getMePort
    ) {

        this.attributeDefinitionRepository = attributeDefinitionRepository;
        this.getMePort = getMePort;
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }
   
    @Override
    public void execute(
        Id id,
        String title,
        Slug slug,
        String description,
        Set<String> categories, 
        Price price, 
        Set<ProductAttributeValue> attributeValues,
        Set<Coupon> coupons,
        Quantity stock,
        Set<String> images,
        Set<String> tags    
    ) {
        Product product = Product.factory(
            getMePort.execute(),
            id,
            title, 
            slug, 
            description, 
            categories, 
            price, 
            attributeValues,
            coupons,
            stock, 
            images, 
            tags
        );

        categoryRepository.isValidTheseCategoryIds(categories);
        
        couponRepository.applyAutomaticCoupons(product);

        List<AttributeDefinition> defaultAttributes = attributeDefinitionRepository
            .getGlobalAttributes();

        List<CategoryAttribute> catAttrs = 
           categoryRepository 
            .getCategoryAttributesWithAttributeDefinitionsByCategoryIds(
                categories
            );
        
        product.validGlobalAttributesAndCategoryAttributes(new HashSet<>(defaultAttributes), new HashSet<>(catAttrs));
      
        productRepository.save(product);
    }
}
