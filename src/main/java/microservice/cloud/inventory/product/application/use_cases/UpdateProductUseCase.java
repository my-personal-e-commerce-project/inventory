package microservice.cloud.inventory.product.application.use_cases;

import java.util.List;
import java.security.InvalidParameterException;
import java.util.Set;

import microservice.cloud.inventory.discount.domain.entity.Discount;
import microservice.cloud.inventory.discount.domain.repository.DiscountRepository;
import microservice.cloud.inventory.product.application.ports.in.UpdateProductUseCasePort;
import microservice.cloud.inventory.shared.application.ports.out.GetMePort;
import microservice.cloud.inventory.product.domain.entity.Product;
import microservice.cloud.inventory.product.domain.entity.ProductAttributeValue;
import microservice.cloud.inventory.product.domain.entity.ProductRepository;
import microservice.cloud.inventory.product.domain.value_objects.Price;
import microservice.cloud.inventory.product.domain.value_objects.Quantity;
import microservice.cloud.inventory.shared.domain.value_objects.Me;
import microservice.cloud.inventory.shared.domain.value_objects.Permission;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;

public class UpdateProductUseCase implements UpdateProductUseCasePort {

    private final ProductRepository productRepository;
    private final DiscountRepository discountRepository;
    private final GetMePort getMePort;

    public UpdateProductUseCase(
        ProductRepository productRepository,
        DiscountRepository discountRepository,
        GetMePort getMePort
    ) {
        this.productRepository = productRepository;
        this.discountRepository = discountRepository;
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
        Set<String> discounts,
        Set<String> tags
    ) {
        Me me = getMePort.execute();

        if(me == null)
            throw new RuntimeException("You do not have permission to perform this action");

        me.IHavePermission(Permission.updateProduct());

        Product p = productRepository.findBySlug(find_slug);
    
        List<Discount> objectDiscounts = null;
        if (discounts != null && !discounts.isEmpty())
           objectDiscounts = discountRepository.getDiscountsByIds(discounts); 

        p.update(
            title, 
            slug, 
            description, 
            categories, 
            price, 
            stock, 
            images, 
            attributes, 
            objectDiscounts, 
            tags
        );

        productRepository.update(p);
    }
}
