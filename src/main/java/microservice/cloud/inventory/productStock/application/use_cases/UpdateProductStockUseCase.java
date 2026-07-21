package microservice.cloud.inventory.productStock.application.use_cases;

import microservice.cloud.inventory.product.domain.entity.Product;
import microservice.cloud.inventory.product.domain.entity.ProductRepository;
import microservice.cloud.inventory.product.domain.value_objects.Quantity;
import microservice.cloud.inventory.productStock.domain.entity.ProductStock;
import microservice.cloud.inventory.productStock.domain.repository.ProductStockRepository;
import microservice.cloud.inventory.shared.domain.value_objects.Id;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;

public class UpdateProductStockUseCase {

    private final ProductStockRepository productStockRepository;
    private final ProductRepository productRepository;

    public UpdateProductStockUseCase(ProductRepository productRepository, ProductStockRepository productStockRepository) {
        this.productStockRepository = productStockRepository;
        this.productRepository = productRepository;
    }

    public void execute(Slug slug, Quantity quantity) {

        Product product = productRepository.findBySlug(slug);

        Id id = product.stockId();

        productStockRepository.updatePessimistic(id, (ProductStock ps) -> {
            ps.updateQuantity(quantity);
        });
    }
}
