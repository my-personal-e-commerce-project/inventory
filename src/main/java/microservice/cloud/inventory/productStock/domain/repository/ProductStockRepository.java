package microservice.cloud.inventory.productStock.domain.repository;

import java.util.function.Consumer;

import microservice.cloud.inventory.productStock.domain.entity.ProductStock;
import microservice.cloud.inventory.shared.domain.value_objects.Id;

public interface ProductStockRepository {

    public void save(ProductStock productStock);
    public void updatePessimistic(Id id, Consumer<ProductStock> function);
}
