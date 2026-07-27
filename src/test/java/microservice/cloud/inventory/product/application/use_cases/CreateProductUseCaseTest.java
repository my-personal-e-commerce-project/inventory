package microservice.cloud.inventory.product.application.use_cases;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import microservice.cloud.inventory.attribute.domain.entity.AttributeDefinition;
import microservice.cloud.inventory.attribute.domain.repository.AttributeDefinitionRepository;
import microservice.cloud.inventory.attribute.domain.value_objects.DataType;
import microservice.cloud.inventory.category.domain.entity.CategoryAttribute;
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
class CreateProductUseCaseTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private AttributeDefinitionRepository attributeDefinitionRepository;
    @Mock
    private GetMePort getMePort;

    @InjectMocks
    private CreateProductUseCase createProductUseCase;

    @Test
    void shouldCreateProductSuccessfullyWhenPermittedAndValid() {
        // GIVEN
        Me me = new Me(Id.generate(), Set.of(Permission.createProduct()));
        when(getMePort.execute()).thenReturn(me);

        // Definimos un atributo global requerido
        Id globalDefId = Id.generate();
        AttributeDefinition globalDef = new AttributeDefinition(globalDefId, "Global", Slug.fromString("global"), DataType.STRING, true);
        when(attributeDefinitionRepository.getGlobalAttributes()).thenReturn(List.of(globalDef));

        // El producto tiene el valor para el atributo global
        ProductAttributeValue pav = new ProductAttributeValue(Id.generate(), globalDefId, "value", null, null, null);
        Product product = new Product(
            Id.generate(), "Product title", Slug.fromString("slug"), "Desc", Set.of("cat-1"), true,
            new Price(10.0), Set.of(pav), new Quantity(5), new HashSet<>(), new HashSet<>()
        );

        when(categoryRepository.getCategoryAttributesWithAttributeDefinitionsByCategoryIds(product.categories()))
            .thenReturn(List.of()); // Sin atributos de categorías para simplificar

        Id stockId = Id.generate();

        // WHEN
        assertDoesNotThrow(() -> createProductUseCase.execute(product, stockId, new Quantity(5)));

        // THEN
        verify(productRepository, times(1)).createProductAndStock(product, stockId, new Quantity(5));
    }

    @Test
    void shouldThrowExceptionWhenUserIsNull() {
        // GIVEN
        when(getMePort.execute()).thenReturn(null);
        Product product = mock(Product.class);

        // WHEN & THEN
        RuntimeException exception = assertThrows(
            RuntimeException.class, 
            () -> createProductUseCase.execute(product)
        );
        assertEquals("You do not have permission to perform this action", exception.getMessage());
        verifyNoInteractions(productRepository, categoryRepository, attributeDefinitionRepository);
    }

    @Test
    void shouldThrowUnauthorizedExceptionWhenUserHasNoPermission() {
        // GIVEN
        Me me = new Me(Id.generate(), Set.of(Permission.deleteProduct())); // Sin permiso de creación
        when(getMePort.execute()).thenReturn(me);
        Product product = mock(Product.class);

        // WHEN & THEN
        assertThrows(
            UnauthorizedException.class, 
            () -> createProductUseCase.execute(product)
        );
        verifyNoInteractions(productRepository, categoryRepository, attributeDefinitionRepository);
    }

    @Test
    void shouldThrowExceptionWhenProductAttributesValidationFails() {
        // GIVEN
        Me me = new Me(Id.generate(), Set.of(Permission.createProduct()));
        when(getMePort.execute()).thenReturn(me);

        // Atributo global faltará
        Id globalDefId = Id.generate();
        AttributeDefinition globalDef = new AttributeDefinition(globalDefId, "Global", Slug.fromString("global"), DataType.STRING, true);
        when(attributeDefinitionRepository.getGlobalAttributes()).thenReturn(List.of(globalDef));

        Product product = new Product(
            Id.generate(), "Product title", Slug.fromString("slug"), "Desc", Set.of("cat-1"), true,
            new Price(10.0), new HashSet<>(), Id.generate(), new Quantity(5), new HashSet<>(), new HashSet<>()
        );

        when(categoryRepository.getCategoryAttributesWithAttributeDefinitionsByCategoryIds(product.categories()))
            .thenReturn(List.of());

        // WHEN & THEN
        assertThrows(
            IllegalStateException.class, 
            () -> createProductUseCase.execute(product)
        );
        verify(productRepository, never()).save(any());
    }
}
