package microservice.cloud.inventory.productStock.application.use_cases;

import microservice.cloud.inventory.product.domain.entity.Product;
import microservice.cloud.inventory.product.domain.entity.ProductRepository;
import microservice.cloud.inventory.productStock.domain.entity.ProductStock;
import microservice.cloud.inventory.productStock.domain.repository.ProductStockRepository;
import microservice.cloud.inventory.shared.application.ports.out.EventPublisher;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;

public class IncrementProductStockUseCase {
    private final ProductStockRepository productStockRepository;
    private final ProductRepository productRepository;
    private final EventPublisher eventPublisher;

    public IncrementProductStockUseCase(ProductStockRepository productStockRepository, ProductRepository productRepository, EventPublisher eventPublisher) {
        this.productStockRepository = productStockRepository;
        this.productRepository = productRepository;
        this.eventPublisher = eventPublisher;
    }

    public void execute(Slug productSlug, int value) {
        Product product  = productRepository.findBySlug(productSlug);

        ProductStock productStock = productStockRepository.findByProductId(product.id());

        productStock.decrementQuantity(value);
        
        product.minStockReached(productStock.quantity());

        productStockRepository.incrementStock(
            product.id(),
            value
        );

        if(product.getEvents() != null && !product.getEvents().isEmpty()) {
            eventPublisher.publish(product.getEvents());
        }
    }
}
