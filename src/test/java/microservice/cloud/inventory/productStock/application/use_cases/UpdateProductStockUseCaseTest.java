package microservice.cloud.inventory.productStock.application.use_cases;

import java.util.HashSet;
import java.util.Set;

import microservice.cloud.inventory.product.domain.entity.Product;
import microservice.cloud.inventory.product.domain.entity.ProductRepository;
import microservice.cloud.inventory.product.domain.value_objects.Price;
import microservice.cloud.inventory.product.domain.value_objects.Quantity;
import microservice.cloud.inventory.productStock.domain.entity.ProductStock;
import microservice.cloud.inventory.productStock.domain.repository.ProductStockRepository;
import microservice.cloud.inventory.shared.application.ports.out.EventPublisher;
import microservice.cloud.inventory.shared.domain.exception.DataNotFound;
import microservice.cloud.inventory.shared.domain.value_objects.Id;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateProductStockUseCaseTest {

    @Mock
    private ProductStockRepository productStockRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private EventPublisher eventPublisher;

    @InjectMocks
    private DecrementProductStockUseCase decrementProductStockUseCase;

    @InjectMocks
    private IncrementProductStockUseCase incrementProductStockUseCase;

    private Product createSampleProduct(Quantity minStock) {
        return new Product(
            Id.generate(),
            "Sample Product",
            Slug.fromString("sample-product"),
            "Description",
            Set.of("cat-1"),
            true,
            new Price(100.0),
            new HashSet<>(),
            minStock,
            Set.of("image1.png"),
            Set.of("tag1")
        );
    }

    @Test
    void shouldIncrementProductStockSuccessfully() {
        // GIVEN
        Slug slug = Slug.fromString("sample-product");
        Product product = createSampleProduct(new Quantity(5));
        ProductStock productStock = new ProductStock(Id.generate(), product.id(), new Quantity(10));

        when(productRepository.findBySlug(slug)).thenReturn(product);
        when(productStockRepository.findByProductId(product.id())).thenReturn(productStock);

        // WHEN
        incrementProductStockUseCase.execute(slug, 50);

        // THEN
        assertEquals(60, productStock.quantity().value());
        verify(productStockRepository).incrementStock(product.id(), 50);
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void shouldDecrementProductStockAndPublishMinStockEventWhenBelowMinStock() {
        // GIVEN
        Slug slug = Slug.fromString("sample-product");
        Product product = createSampleProduct(new Quantity(10));
        ProductStock productStock = new ProductStock(Id.generate(), product.id(), new Quantity(20));

        when(productRepository.findBySlug(slug)).thenReturn(product);
        when(productStockRepository.findByProductId(product.id())).thenReturn(productStock);

        // WHEN
        decrementProductStockUseCase.execute(slug, 18);

        // THEN
        assertEquals(2, productStock.quantity().value());
        assertFalse(product.isActive());
        verify(productStockRepository).decrementStock(product.id(), 18);
        verify(eventPublisher, times(1)).publish(any());
    }

    @Test
    void shouldThrowExceptionWhenProductNotFoundOnUpdate() {
        // GIVEN
        Slug slug = Slug.fromString("non-existent-product");
        when(productRepository.findBySlug(slug)).thenThrow(new DataNotFound("Product not found"));

        // WHEN & THEN
        assertThrows(DataNotFound.class, () -> decrementProductStockUseCase.execute(slug, 10));
        verifyNoInteractions(productStockRepository, eventPublisher);
    }
}
