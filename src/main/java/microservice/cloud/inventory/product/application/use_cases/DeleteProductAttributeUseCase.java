package microservice.cloud.inventory.product.application.use_cases;

import microservice.cloud.inventory.product.application.ports.in.DeleteProductAttributeUseCasePort;
import microservice.cloud.inventory.shared.application.ports.in.GetMePort;
import microservice.cloud.inventory.shared.application.ports.out.EventPublishedPort;
import microservice.cloud.inventory.product.domain.entity.Product;
import microservice.cloud.inventory.product.domain.entity.ProductRepository;
import microservice.cloud.inventory.shared.domain.value_objects.Id;

public class DeleteProductAttributeUseCase implements DeleteProductAttributeUseCasePort {

    private ProductRepository productRepository;
    private EventPublishedPort eventPublishedPort;
    private GetMePort getMePort;

    public DeleteProductAttributeUseCase(
        ProductRepository productRepository,
        EventPublishedPort eventPublishedPort,
        GetMePort getMePort
    ) {
        this.productRepository = productRepository;
        this.eventPublishedPort = eventPublishedPort;
        this.getMePort = getMePort;
    }

    @Override
    public void execute(Id productId, Id productAttributeId) {
        Product product = productRepository.findById(productId);

        product.removeAttribute(getMePort.execute(), productAttributeId);

        productRepository.removeProductAttribute(productId, productAttributeId);

        eventPublishedPort.publish(product.events());
    } 
}
