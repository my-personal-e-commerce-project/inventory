package microservice.cloud.inventory.productStock.domain.repository;

import microservice.cloud.inventory.productStock.domain.entity.ProductStock;
import microservice.cloud.inventory.shared.domain.value_objects.Id;

public interface ProductStockRepository {

    public ProductStock findByProductId(Id productId);
    public void save(ProductStock productStock);
    public void updateIfExists(Id id, ProductStock productStock);
}
