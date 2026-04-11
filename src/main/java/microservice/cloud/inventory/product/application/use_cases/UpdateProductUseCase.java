package microservice.cloud.inventory.product.application.use_cases;

import java.security.InvalidParameterException;
import java.util.Set;

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

    private ProductRepository productRepository;
    private GetMePort getMePort;

    public UpdateProductUseCase(
        ProductRepository productRepository,
        GetMePort getMePort
    ) {
        this.productRepository = productRepository;
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

        if(categories == null || categories.size() < 1)
            throw new InvalidParameterException("Products must have at least one category");

        me.IHavePermission(Permission.updateProduct());

        Product p = productRepository.findBySlug(find_slug);

        p.update(
            title, 
            slug, 
            description, 
            categories, 
            price, 
            stock, 
            images, 
            attributes, 
            discounts, 
            tags
        );

        productRepository.update(p);
    }
}
