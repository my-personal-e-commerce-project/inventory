package microservice.cloud.inventory.product.application.use_cases;

import java.util.HashSet;
import java.util.Set;

import microservice.cloud.inventory.category.domain.repository.CategoryRepository;
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
class DeleteProductAttributeUseCaseTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private GetMePort getMePort;

    @InjectMocks
    private DeleteProductAttributeUseCase deleteProductAttributeUseCase;

    @Test
    void shouldRemoveAttributeSuccessfullyWhenPermitted() {
        // GIVEN
        Me me = new Me(Id.generate(), Set.of(Permission.updateProduct()));
        when(getMePort.execute()).thenReturn(me);

        Slug findSlug = Slug.fromString("product-slug");
        Id attrId = Id.generate();
        Id defId = Id.generate();
        ProductAttributeValue pav = new ProductAttributeValue(attrId, defId, "value", null, null, null);

        Product product = new Product(
            Id.generate(), "Product title", findSlug, "Desc", Set.of("cat-1"), true,
            new Price(10.0), Set.of(pav), Id.generate(), new Quantity(5), new HashSet<>(), new HashSet<>()
        );
        when(productRepository.findBySlug(findSlug)).thenReturn(product);
        when(productRepository.findProductAttributeValueById(attrId)).thenReturn(pav);
        when(categoryRepository.getCategoryAttributeByAttributeDefinitionId(defId)).thenReturn(null);

        // WHEN
        Product result = deleteProductAttributeUseCase.execute(findSlug, attrId);

        // THEN
        assertNotNull(result);
        assertTrue(result.attributeValues().isEmpty());
        verify(productRepository, times(1)).update(product);
    }

    @Test
    void shouldThrowExceptionWhenUserIsNull() {
        // GIVEN
        when(getMePort.execute()).thenReturn(null);

        // WHEN & THEN
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> deleteProductAttributeUseCase.execute(Slug.fromString("slug"), Id.generate())
        );
        assertEquals("You do not have permission to perform this action", exception.getMessage());
        verifyNoInteractions(productRepository, categoryRepository);
    }

    @Test
    void shouldThrowUnauthorizedExceptionWhenUserHasNoPermission() {
        // GIVEN
        Me me = new Me(Id.generate(), Set.of(Permission.createProduct())); // Diferente permiso
        when(getMePort.execute()).thenReturn(me);

        // WHEN & THEN
        assertThrows(
            UnauthorizedException.class,
            () -> deleteProductAttributeUseCase.execute(Slug.fromString("slug"), Id.generate())
        );
        verifyNoInteractions(productRepository, categoryRepository);
    }
}
