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
import microservice.cloud.inventory.product.application.ports.in.UpdateProductUseCasePort;
import microservice.cloud.inventory.shared.application.ports.out.GetMePort;
import microservice.cloud.inventory.product.domain.entity.Product;
import microservice.cloud.inventory.product.domain.entity.ProductAttributeValue;
import microservice.cloud.inventory.product.domain.entity.ProductRepository;
import microservice.cloud.inventory.product.domain.value_objects.Price;
import microservice.cloud.inventory.product.domain.value_objects.Quantity;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;

public class UpdateProductUseCase implements UpdateProductUseCasePort {

    private ProductRepository productRepository;
    private CategoryRepository categoryRepository;
    private AttributeDefinitionRepository attributeDefinitionRepository;
    private CouponRepository couponRepository;
    private GetMePort getMePort;

    public UpdateProductUseCase(
        ProductRepository productRepository,
        CategoryRepository categoryRepository,
        AttributeDefinitionRepository attributeDefinitionRepository,
        CouponRepository couponRepository,
        GetMePort getMePort
    ) {
        this.productRepository = productRepository;
        this.attributeDefinitionRepository = attributeDefinitionRepository;
        this.categoryRepository = categoryRepository;
        this.couponRepository = couponRepository;
        this.getMePort = getMePort;
    }

    @Override
    public void execute(
        Slug find_slug,
        String title, 
        Slug slug, 
        String description,
        Set<String> categories,
        Price price,
        Quantity stock,
        Set<String> images,
        Set<ProductAttributeValue> attributes,
        Set<String> coupons,
        Set<String> tags
    ) {
        Product p = productRepository.findBySlug(find_slug);

        List<Coupon> foundCoupons = couponRepository.getCouponsByIds(coupons);

        p.update(
            getMePort.execute(),
            title, 
            slug, 
            description, 
            categories, 
            price, 
            attributes,
            foundCoupons,
            stock, 
            images, 
            tags
        );

        categoryRepository.isValidTheseCategoryIds(categories);

        List<AttributeDefinition> defaultAttributes = attributeDefinitionRepository
            .getGlobalAttributes();

        List<CategoryAttribute> catAttrs = 
           categoryRepository 
            .getCategoryAttributesWithAttributeDefinitionsByCategoryIds(
                categories
            );
     
        p.validGlobalAttributesAndCategoryAttributes(new HashSet<>(defaultAttributes), new HashSet<>(catAttrs));

        productRepository.update(p);
    }
}
