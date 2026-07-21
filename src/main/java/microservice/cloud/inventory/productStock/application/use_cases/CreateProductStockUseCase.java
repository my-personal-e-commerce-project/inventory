package microservice.cloud.inventory.productStock.application.use_cases;

import microservice.cloud.inventory.productStock.domain.entity.ProductStock;
import microservice.cloud.inventory.productStock.domain.repository.ProductStockRepository;

public class CreateProductStockUseCase {

    private final ProductStockRepository productStockRepository;

    public CreateProductStockUseCase(ProductStockRepository productStockRepository) {
        this.productStockRepository = productStockRepository;
    }

    public void execute(ProductStock productStock) {
        productStockRepository.save(productStock);
    }
}
