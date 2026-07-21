package microservice.cloud.inventory.productStock.application.use_cases;

import microservice.cloud.inventory.productStock.domain.entity.ProductStock;
import microservice.cloud.inventory.productStock.domain.repository.ProductStockRepository;
import microservice.cloud.inventory.shared.domain.value_objects.Id;

public class DecrementProductStockUseCase {

    private final ProductStockRepository productStockRepository;

    public DecrementProductStockUseCase(ProductStockRepository productStockRepository) {
        this.productStockRepository = productStockRepository;
    }

    public void execute(Id id, int value) {
        productStockRepository.updatePessimistic(id, (ProductStock ps) -> {
            ps.decrementQuantity(value);
        });
    }
}
