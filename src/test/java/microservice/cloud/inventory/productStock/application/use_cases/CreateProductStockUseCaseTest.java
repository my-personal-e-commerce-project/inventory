package microservice.cloud.inventory.productStock.application.use_cases;

import microservice.cloud.inventory.product.domain.value_objects.Quantity;
import microservice.cloud.inventory.productStock.domain.entity.ProductStock;
import microservice.cloud.inventory.productStock.domain.repository.ProductStockRepository;
import microservice.cloud.inventory.shared.domain.value_objects.Id;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CreateProductStockUseCaseTest {

    @Mock
    private ProductStockRepository productStockRepository;

    @InjectMocks
    private CreateProductStockUseCase createProductStockUseCase;

    @Test
    void shouldCreateProductStockSuccessfully() {
        ProductStock productStock = new ProductStock(Id.generate(), Id.generate(), new Quantity(20));

        createProductStockUseCase.execute(productStock);

        verify(productStockRepository).save(productStock);
    }
}
