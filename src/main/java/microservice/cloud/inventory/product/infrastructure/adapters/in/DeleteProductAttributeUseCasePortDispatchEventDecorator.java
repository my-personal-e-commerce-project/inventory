package microservice.cloud.inventory.product.infrastructure.adapters.in;

import java.util.List;

import lombok.RequiredArgsConstructor;
import microservice.cloud.inventory.product.application.ports.in.DeleteProductAttributeUseCasePort;
import microservice.cloud.inventory.product.domain.entity.Product;
import microservice.cloud.inventory.product.infrastructure.dtos.event.ProductUpdatedEvent;
import microservice.cloud.inventory.shared.application.ports.out.EventPublishedPort;
import microservice.cloud.inventory.shared.domain.value_objects.Id;

@RequiredArgsConstructor
public class DeleteProductAttributeUseCasePortDispatchEventDecorator implements DeleteProductAttributeUseCasePort{

    private final DeleteProductAttributeUseCasePort deleteProductAttributeUseCasePort;
    private final EventPublishedPort eventPublishedPort;
   
    @Override
    public Product execute(Id productId, Id productAttributeId) {
        Product product = deleteProductAttributeUseCasePort
            .execute(productId, productAttributeId);

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
