package microservice.cloud.inventory.productStock.infrastructure.adapters;

import microservice.cloud.inventory.product.domain.entity.ProductRepository;
import microservice.cloud.inventory.productStock.application.use_cases.CreateProductStockUseCase;
import microservice.cloud.inventory.productStock.application.use_cases.DecrementProductStockUseCase;
import microservice.cloud.inventory.productStock.application.use_cases.UpdateProductStockUseCase;
import microservice.cloud.inventory.productStock.domain.repository.ProductStockRepository;
import microservice.cloud.inventory.shared.application.ports.out.EventPublisher;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class ProductStockConfigAdapterTest {

    @Mock
    private ProductStockRepository productStockRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private EventPublisher eventPublisher;

    private final ProductStockConfigAdapter adapter = new ProductStockConfigAdapter();

    @Test
    void shouldCreateDecrementProductStockUseCaseBean() {
        DecrementProductStockUseCase useCase = adapter.decrementProductStockUseCase(
            productStockRepository, productRepository, eventPublisher
        );
        assertNotNull(useCase);
    }

    @Test
    void shouldCreateUpdateProductStockUseCaseBean() {
        UpdateProductStockUseCase useCase = adapter.updateProductStockUseCase(
            productStockRepository, productRepository, eventPublisher
        );
        assertNotNull(useCase);
    }

    @Test
    void shouldCreateCreateProductStockUseCaseBean() {
        CreateProductStockUseCase useCase = adapter.createProductStockUseCase(productStockRepository);
        assertNotNull(useCase);
    }
}
