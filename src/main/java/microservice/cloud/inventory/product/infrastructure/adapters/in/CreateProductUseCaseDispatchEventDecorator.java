package microservice.cloud.inventory.product.infrastructure.adapters.in;

import java.util.List;

import lombok.RequiredArgsConstructor;
import microservice.cloud.inventory.product.application.ports.in.CreateProductUseCasePort;
import microservice.cloud.inventory.product.domain.entity.Product;
import microservice.cloud.inventory.product.domain.entity.ProductAttributeValue;
import microservice.cloud.inventory.product.domain.value_objects.Price;
import microservice.cloud.inventory.product.domain.value_objects.Quantity;
import microservice.cloud.inventory.product.infrastructure.dtos.event.ProductCreatedEvent;
import microservice.cloud.inventory.shared.application.ports.out.EventPublishedPort;
import microservice.cloud.inventory.shared.domain.value_objects.Id;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;

@RequiredArgsConstructor
public class CreateProductUseCaseDispatchEventDecorator implements CreateProductUseCasePort {

    private final CreateProductUseCasePort createProductUseCasePort;
    private final EventPublishedPort eventPublishedPort;

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
        Product product = createProductUseCasePort
            .execute(
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
 
        eventPublishedPort.publish(
            List.of(
                new ProductCreatedEvent(
                    product.id().value(), 
                    product.title(), 
                    product.slug().value(), 
                    product.description(), 
                    product.categories(), 
                    product.price().value(), 
                    product.stock().value(), 
                    product.images(), 
                    product.attributeValues(), 
                    product.tags()
                )
            )
        );

        return product;
       
    } 
}
