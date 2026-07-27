package microservice.cloud.inventory.product.application.use_cases;

import java.util.Set;

import microservice.cloud.inventory.shared.application.ports.out.EventPublisher;
import microservice.cloud.inventory.shared.application.ports.out.GetMePort;
import microservice.cloud.inventory.product.domain.entity.Product;
import microservice.cloud.inventory.product.domain.entity.ProductAttributeValue;
import microservice.cloud.inventory.product.domain.entity.ProductRepository;
import microservice.cloud.inventory.product.domain.value_objects.Price;
import microservice.cloud.inventory.product.domain.value_objects.Quantity;
import microservice.cloud.inventory.shared.domain.value_objects.Me;
import microservice.cloud.inventory.shared.domain.value_objects.Permission;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;

public class UpdateProductUseCase {

    private final ProductRepository productRepository;
    private final EventPublisher eventPublisher;
    private final GetMePort getMePort;

    public UpdateProductUseCase(
        ProductRepository productRepository,
        EventPublisher eventPublisher,
        GetMePort getMePort
    ) {
        this.productRepository = productRepository;
        this.eventPublisher = eventPublisher;
        this.getMePort = getMePort;
    }

    public void execute(
        Slug find_slug,
        String title, 
        Slug slug, 
        String description,
        Set<String> categories,
        boolean isActive,
        Price price,
        Quantity minStock,
        Set<String> images,
        Set<ProductAttributeValue> attributes,
        Set<String> tags
    ) {
        Me me = getMePort.execute();

        if(me == null)
            throw new RuntimeException("You do not have permission to perform this action");

        me.IHavePermission(Permission.updateProduct());

        Product product = productRepository.findBySlug(find_slug);
    
        product = productRepository.updateIfExists(product.id(), (p) -> {
            p.update(
                title, 
                slug, 
                description, 
                categories,
                isActive,
                price,
                minStock,
                images, 
                attributes, 
                tags
            );
        });

        if(product.getEvents() != null && !product.getEvents().isEmpty()) {
            eventPublisher.publish(product.getEvents());
        }
    }
}

