package microservice.cloud.inventory.product.application.use_cases;

import microservice.cloud.inventory.product.application.ports.in.DeleteProductUseCasePort;
import microservice.cloud.inventory.shared.application.ports.put.EventPublishedPort;
import microservice.cloud.inventory.product.domain.entity.Product;
import microservice.cloud.inventory.product.domain.entity.ProductRepository;
import microservice.cloud.inventory.shared.domain.value_objects.Id;

public class DeleteProductUseCase implements DeleteProductUseCasePort {

    private ProductRepository productRepository;
    private EventPublishedPort eventPublishedPort;

    public DeleteProductUseCase(
        ProductRepository productRepository,
        EventPublishedPort eventPublishedPort
    ) {
        this.productRepository = productRepository;
        this.eventPublishedPort = eventPublishedPort;
    }

    @Override
    public void execute(Id id) {
        Product product = productRepository.findById(id);

        product.delete();

        productRepository.delete(product);

        eventPublishedPort.publish(product.events());
    }
}
