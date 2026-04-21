package microservice.cloud.inventory.attribute.infrastructure.dapters;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import lombok.RequiredArgsConstructor;
import microservice.cloud.inventory.attribute.application.ports.out.AsynchronousBulkCreationOfDefaultValuesForProductAttributes;
import microservice.cloud.inventory.attribute.domain.entity.AttributeDefinition;
import microservice.cloud.inventory.product.domain.entity.ProductRepository;

@RequiredArgsConstructor
@Component
public class AsynchronousBulkCreationOfDefaultValuesForProductAttributesImpl implements AsynchronousBulkCreationOfDefaultValuesForProductAttributes {

    private final ProductRepository productRepository;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    @Override
    public void execute(AttributeDefinition event) {
        productRepository.massCreateDefaultProductAttributeValues(
            event.id(),
            event.type()
        );
    }
}
