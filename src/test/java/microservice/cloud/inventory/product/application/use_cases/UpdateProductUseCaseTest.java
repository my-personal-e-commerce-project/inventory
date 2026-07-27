package microservice.cloud.inventory.product.application.use_cases;

import java.util.HashSet;
import java.util.Set;

import microservice.cloud.inventory.product.domain.entity.Product;
import microservice.cloud.inventory.product.domain.entity.ProductAttributeValue;
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
class UpdateProductUseCaseTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private GetMePort getMePort;

    @InjectMocks
    private UpdateProductUseCase updateProductUseCase;

    @Test
    void shouldUpdateProductSuccessfullyWhenPermitted() {
        // GIVEN
        Me me = new Me(Id.generate(), Set.of(Permission.updateProduct()));
        when(getMePort.execute()).thenReturn(me);

        Slug findSlug = Slug.fromString("existing-product");
        
        // Creamos un producto existente
        Id attrId = Id.generate();
        Id defId = Id.generate();
        ProductAttributeValue pav = new ProductAttributeValue(attrId, defId, "value", null, null, null);
        Product product = new Product(
            Id.generate(), "Old Title", findSlug, "Old Desc", Set.of("cat-1"), true,
            new Price(10.0), Set.of(pav), new Quantity(5), new HashSet<>(), new HashSet<>()
        );
        when(productRepository.findBySlug(findSlug)).thenReturn(product);

        // Nuevos datos para actualizar
        String newTitle = "New Title";
        Slug newSlug = Slug.fromString("new-title");
        String newDesc = "New Desc";
        Set<String> newCats = Set.of("cat-2");
        Price newPrice = new Price(15.0);
        Quantity newStock = new Quantity(10);
        Set<ProductAttributeValue> updatedAttrs = Set.of(new ProductAttributeValue(attrId, defId, "new-value", null, null, null));

        // WHEN
        assertDoesNotThrow(() -> updateProductUseCase.execute(
            findSlug, newTitle, newSlug, newDesc, newCats, false, newPrice, newStock, new HashSet<>(), updatedAttrs, new HashSet<>()
        ));

        // THEN
        verify(productRepository, times(1)).update(product);
        assertEquals(newTitle, product.title());
        assertEquals("new-title", product.slug().value());
        assertFalse(product.isActive());
    }

    @Test
    void shouldThrowExceptionWhenUserIsNull() {
        // GIVEN
        when(getMePort.execute()).thenReturn(null);

        // WHEN & THEN
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> updateProductUseCase.execute(
                Slug.fromString("slug"), "Title", Slug.fromString("slug"), "Desc", Set.of("cat"), 
                true, new Price(1.0), new Quantity(5), new HashSet<>(), new HashSet<>(), new HashSet<>()
            )
        );
        assertEquals("You do not have permission to perform this action", exception.getMessage());
        verifyNoInteractions(productRepository);
    }

    @Test
    void shouldThrowUnauthorizedExceptionWhenUserHasNoPermission() {
        // GIVEN
        Me me = new Me(Id.generate(), Set.of(Permission.createProduct())); // Diferente permiso
        when(getMePort.execute()).thenReturn(me);

        // WHEN & THEN
        assertThrows(
            UnauthorizedException.class,
            () -> updateProductUseCase.execute(
                Slug.fromString("slug"), "Title", Slug.fromString("slug"), "Desc", Set.of("cat"), 
                true, new Price(1.0), new Quantity(5), new HashSet<>(), new HashSet<>(), new HashSet<>()
            )
        );
        verifyNoInteractions(productRepository);
    }
}
