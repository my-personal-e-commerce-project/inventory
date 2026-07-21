package microservice.cloud.inventory.productStock.application.use_cases;

import java.util.function.Consumer;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DecrementProductStockUseCaseTest {

    @Mock
    private ProductStockRepository productStockRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private EventPublisher eventPublisher;

    @InjectMocks
    private DecrementProductStockUseCase decrementProductStockUseCase;

    private Product createSampleProduct(Id stockId, Quantity minStock) {
        return new Product(
            Id.generate(),
            "Sample Product",
            Slug.fromString("sample-product"),
            "Description",
            Set.of("cat-1"),
            true,
            new Price(100.0),
            new HashSet<>(),
            stockId,
            minStock,
            Set.of("image1.png"),
            Set.of("tag1")
        );
    }

    @Test
    void shouldDecrementProductStockSuccessfullyWhenMinStockNotReached() {
        // GIVEN
        Slug slug = Slug.fromString("sample-product");
        Id stockId = Id.generate();
        Product product = createSampleProduct(stockId, new Quantity(5));
        ProductStock productStock = new ProductStock(stockId, new Quantity(20));

        when(productRepository.findBySlug(slug)).thenReturn(product);

        doAnswer(invocation -> {
            Consumer<ProductStock> consumer = invocation.getArgument(1);
            consumer.accept(productStock);
            return null;
        }).when(productStockRepository).updatePessimistic(eq(stockId), any());

        // WHEN
        decrementProductStockUseCase.execute(slug, 10);

        // THEN
        assertEquals(10, productStock.quantity().value());
        verify(productRepository).update(product);
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void shouldDecrementProductStockAndTriggerMinStockAlertWhenMinStockReached() {
        // GIVEN
        Slug slug = Slug.fromString("sample-product");
        Id stockId = Id.generate();
        Product product = createSampleProduct(stockId, new Quantity(5));
        ProductStock productStock = new ProductStock(stockId, new Quantity(8));

        when(productRepository.findBySlug(slug)).thenReturn(product);

        doAnswer(invocation -> {
            Consumer<ProductStock> consumer = invocation.getArgument(1);
            consumer.accept(productStock);
            return null;
        }).when(productStockRepository).updatePessimistic(eq(stockId), any());

        // WHEN
        decrementProductStockUseCase.execute(slug, 5);

        // THEN
        assertEquals(3, productStock.quantity().value());
        assertFalse(product.isActive());
        verify(productRepository).update(product);
        verify(eventPublisher, times(1)).publish(product.getEvents());
    }

    @Test
    void shouldThrowExceptionWhenProductNotFound() {
        // GIVEN
        Slug slug = Slug.fromString("non-existent-product");
        when(productRepository.findBySlug(slug)).thenThrow(new DataNotFound("Product not found"));

        // WHEN & THEN
        assertThrows(DataNotFound.class, () -> decrementProductStockUseCase.execute(slug, 5));
        verifyNoInteractions(productStockRepository, eventPublisher);
    }

    @Test
    void shouldThrowExceptionWhenDecrementExceedsAvailableQuantity() {
        // GIVEN
        Slug slug = Slug.fromString("sample-product");
        Id stockId = Id.generate();
        Product product = createSampleProduct(stockId, new Quantity(5));
        ProductStock productStock = new ProductStock(stockId, new Quantity(3));

        when(productRepository.findBySlug(slug)).thenReturn(product);

        doAnswer(invocation -> {
            Consumer<ProductStock> consumer = invocation.getArgument(1);
            consumer.accept(productStock);
            return null;
        }).when(productStockRepository).updatePessimistic(eq(stockId), any());

        // WHEN & THEN
        assertThrows(IllegalArgumentException.class, () -> decrementProductStockUseCase.execute(slug, 5));
        verify(eventPublisher, never()).publish(any());
    }
}
