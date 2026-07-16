package microservice.cloud.inventory.product.application.use_cases;

import java.util.HashSet;
import java.util.Set;

import microservice.cloud.inventory.product.domain.entity.Product;
import microservice.cloud.inventory.product.domain.entity.ProductRepository;
import microservice.cloud.inventory.product.domain.value_objects.Price;
import microservice.cloud.inventory.product.domain.value_objects.Quantity;
import microservice.cloud.inventory.shared.application.ports.out.GetMePort;
import microservice.cloud.inventory.shared.domain.exception.UnauthorizedException;
import microservice.cloud.inventory.shared.domain.value_objects.Id;
import microservice.cloud.inventory.shared.domain.value_objects.Me;
import microservice.cloud.inventory.shared.domain.value_objects.Permission;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteProductUseCaseTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private GetMePort getMePort;

    @InjectMocks
    private DeleteProductUseCase deleteProductUseCase;

    @Test
    void shouldDeleteProductSuccessfullyWhenPermitted() {
        // GIVEN
        Me me = new Me(Id.generate(), Set.of(Permission.deleteProduct()));
        when(getMePort.execute()).thenReturn(me);

        Slug findSlug = Slug.fromString("product-to-delete");
        Product product = new Product(
            Id.generate(), "Product title", findSlug, "Desc", Set.of("cat-1"), true,
            new Price(10.0), new HashSet<>(), new Quantity(5), new Quantity(5), new HashSet<>(), new HashSet<>()
        );
        when(productRepository.findBySlug(findSlug)).thenReturn(product);

        // WHEN
        assertDoesNotThrow(() -> deleteProductUseCase.execute(findSlug));

        // THEN
        verify(productRepository, times(1)).findBySlug(findSlug);
        verify(productRepository, times(1)).delete(product);
    }

    @Test
    void shouldThrowExceptionWhenUserIsNull() {
        // GIVEN
        when(getMePort.execute()).thenReturn(null);

        // WHEN & THEN
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> deleteProductUseCase.execute(Slug.fromString("slug"))
        );
        assertEquals("You do not have permission to perform this action", exception.getMessage());
        verifyNoInteractions(productRepository);
    }

    @Test
    void shouldThrowUnauthorizedExceptionWhenUserHasNoPermission() {
        // GIVEN
        Me me = new Me(Id.generate(), Set.of(Permission.createProduct())); // Different permission
        when(getMePort.execute()).thenReturn(me);

        // WHEN & THEN
        assertThrows(
            UnauthorizedException.class,
            () -> deleteProductUseCase.execute(Slug.fromString("slug"))
        );
        verifyNoInteractions(productRepository);
    }
}
