package microservice.cloud.inventory.productStock.application.use_cases;

import microservice.cloud.inventory.product.domain.entity.Product;
import microservice.cloud.inventory.product.domain.entity.ProductRepository;
import microservice.cloud.inventory.product.domain.value_objects.Quantity;
import microservice.cloud.inventory.productStock.domain.entity.ProductStock;
import microservice.cloud.inventory.productStock.domain.repository.ProductStockRepository;
import microservice.cloud.inventory.shared.application.ports.out.EventPublisher;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;

public class UpdateProductStockUseCase {
    private final ProductStockRepository productStockRepository;
    private final ProductRepository productRepository;
    private final EventPublisher eventPublisher;

    public UpdateProductStockUseCase(ProductRepository productRepository, ProductStockRepository productStockRepository, EventPublisher eventPublisher) {
        this.productStockRepository = productStockRepository;
        this.productRepository = productRepository;
        this.eventPublisher = eventPublisher;
    }

    public void execute(Slug slug, Quantity quantity) {
        Product product = productRepository.findBySlug(slug);

        productStockRepository.updatePessimistic(product.id(), (ProductStock ps) -> {

            ps.updateQuantity(quantity);

            product.minStockReached(ps.quantity());
        });

        productRepository.update(product);

        if(product.getEvents() != null && !product.getEvents().isEmpty()) {
            eventPublisher.publish(product.getEvents());
        }
    }
}
