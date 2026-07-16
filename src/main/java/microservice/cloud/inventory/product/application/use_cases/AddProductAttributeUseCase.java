package microservice.cloud.inventory.product.application.use_cases;

import microservice.cloud.inventory.shared.application.ports.out.GetMePort;
import microservice.cloud.inventory.product.domain.entity.Product;
import microservice.cloud.inventory.product.domain.entity.ProductAttributeValue;
import microservice.cloud.inventory.product.domain.entity.ProductRepository;
import microservice.cloud.inventory.shared.domain.value_objects.Me;
import microservice.cloud.inventory.shared.domain.value_objects.Permission;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;

public class AddProductAttributeUseCase {

    private ProductRepository productRepository;
    private GetMePort getMePort;

    public AddProductAttributeUseCase(
        ProductRepository productRepository,
        GetMePort getMePort
    ) {
        this.productRepository = productRepository;
        this.getMePort = getMePort;
    }

    public Product execute(Slug find_slug, ProductAttributeValue productAttributeValue) {
        Me me = getMePort.execute();

        if(me == null)
            throw new RuntimeException("You do not have permission to perform this action");

        me.IHavePermission(Permission.updateProduct());

        Product product = productRepository.findBySlug(find_slug);
        
        product.addProductAttribute(productAttributeValue);
        
        productRepository.update(product);
        
        return product;
    } 
}
