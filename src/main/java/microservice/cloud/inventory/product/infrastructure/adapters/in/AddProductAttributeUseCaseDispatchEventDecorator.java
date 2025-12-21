package microservice.cloud.inventory.product.infrastructure.adapters.in;

import java.util.List;

import lombok.RequiredArgsConstructor;
import microservice.cloud.inventory.product.application.ports.in.AddProductAttributeUseCasePort;
import microservice.cloud.inventory.product.domain.entity.Product;
import microservice.cloud.inventory.product.domain.entity.ProductAttributeValue;
import microservice.cloud.inventory.product.infrastructure.dtos.event.ProductUpdatedEvent;
import microservice.cloud.inventory.shared.application.ports.out.EventPublishedPort;
import microservice.cloud.inventory.shared.domain.value_objects.Id;

@RequiredArgsConstructor
public class AddProductAttributeUseCaseDispatchEventDecorator implements AddProductAttributeUseCasePort{

    private final AddProductAttributeUseCasePort addProductAttributeUseCasePort;
    private final EventPublishedPort eventPublishedPort;

    @Override
    public Product execute(Id productId, ProductAttributeValue productAttributeValue) {
        Product product = addProductAttributeUseCasePort.execute(productId, productAttributeValue);

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
