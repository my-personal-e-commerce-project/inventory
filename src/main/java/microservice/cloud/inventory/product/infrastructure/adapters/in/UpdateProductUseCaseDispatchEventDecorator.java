package microservice.cloud.inventory.product.infrastructure.adapters.in;

import java.util.List;

import lombok.RequiredArgsConstructor;
import microservice.cloud.inventory.product.application.ports.in.UpdateProductUseCasePort;
import microservice.cloud.inventory.product.domain.entity.Product;
import microservice.cloud.inventory.product.domain.entity.ProductAttributeValue;
import microservice.cloud.inventory.product.domain.value_objects.Price;
import microservice.cloud.inventory.product.domain.value_objects.Quantity;
import microservice.cloud.inventory.product.infrastructure.dtos.event.ProductUpdatedEvent;
import microservice.cloud.inventory.shared.application.ports.out.EventPublishedPort;
import microservice.cloud.inventory.shared.domain.value_objects.Id;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;

@RequiredArgsConstructor
public class UpdateProductUseCaseDispatchEventDecorator implements UpdateProductUseCasePort {

    private final UpdateProductUseCasePort updateProductUseCasePort;
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
        Product product = updateProductUseCasePort.execute(
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
                new ProductUpdatedEvent(
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
