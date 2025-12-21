package microservice.cloud.inventory.product.infrastructure.adapters.in;

import java.util.List;

import lombok.RequiredArgsConstructor;
import microservice.cloud.inventory.product.application.ports.in.DeleteProductUseCasePort;
import microservice.cloud.inventory.product.infrastructure.dtos.event.ProductDeletedEvent;
import microservice.cloud.inventory.shared.application.ports.out.EventPublishedPort;
import microservice.cloud.inventory.shared.domain.value_objects.Id;

@RequiredArgsConstructor
public class DeleteProductUseCaseDispatchEventDecorator implements DeleteProductUseCasePort {

    private final DeleteProductUseCasePort deleteProductAttributeUseCasePort;
    private final EventPublishedPort eventPublishedPort;

    @Override
    public void execute(Id id) {
        deleteProductAttributeUseCasePort.execute(id);

        eventPublishedPort.publish(
            List.of(
                new ProductDeletedEvent(
                    id.value()
                )
            )
        );
    }
}
