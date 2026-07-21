package microservice.cloud.inventory.productStock.infrastructure.persistation.repository;

import java.util.function.Consumer;

import org.springframework.stereotype.Repository;

import microservice.cloud.inventory.productStock.domain.entity.ProductStock;
import microservice.cloud.inventory.productStock.domain.repository.ProductStockRepository;
import microservice.cloud.inventory.shared.domain.value_objects.Id;

@Repository
public class ProductStockRepositoryJdbcAdapter implements ProductStockRepository {
    
    @Override
    public void save(ProductStock productStock) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updatePessimistic(Id id, Consumer<ProductStock> function) {
        // TODO Auto-generated method stub
        
    }
}
