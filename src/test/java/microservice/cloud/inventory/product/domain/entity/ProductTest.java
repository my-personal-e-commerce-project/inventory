package microservice.cloud.inventory.product.domain.entity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import microservice.cloud.inventory.attribute.domain.entity.AttributeDefinition;
import microservice.cloud.inventory.attribute.domain.value_objects.DataType;
import microservice.cloud.inventory.category.domain.entity.CategoryAttribute;
import microservice.cloud.inventory.product.domain.exception.InvalidProductException;
import microservice.cloud.inventory.product.domain.value_objects.Price;
import microservice.cloud.inventory.product.domain.value_objects.Quantity;
import microservice.cloud.inventory.shared.domain.exception.DataNotFound;
import microservice.cloud.inventory.shared.domain.exception.UnauthorizedException;
import microservice.cloud.inventory.shared.domain.value_objects.Id;
import microservice.cloud.inventory.shared.domain.value_objects.Me;
import microservice.cloud.inventory.shared.domain.value_objects.Permission;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    private Product createDefaultProduct() {
        return new Product(
            Id.generate(),
            "Sample Product",
            Slug.fromString("sample-product"),
            "Description",
            Set.of("cat-1"),
            true,
            new Price(100.0),
            new HashSet<>(),
            new Quantity(10),
            Set.of("image1.png"),
            Set.of("tag1")
        );
    }

    @Test
    void shouldCreateProductWithValidValues() {
        Id id = Id.generate();
        Product product = new Product(
            id,
            "Sample Product",
            Slug.fromString("sample-product"),
            "Description",
            Set.of("cat-1"),
            true,
            new Price(100.0),
            new HashSet<>(),
            new Quantity(10),
            Set.of("image1.png"),
            Set.of("tag1")
        );

        assertEquals(id, product.id());
        assertEquals("Sample Product", product.title());
        assertEquals("sample-product", product.slug().value());
        assertEquals("Description", product.description());
        assertEquals(Set.of("cat-1"), product.categories());
        assertTrue(product.isActive());
        assertEquals(100.0, product.price().value());
        assertEquals(10, product.stock().value());
        assertEquals(Set.of("image1.png"), product.images());
        assertEquals(Set.of("tag1"), product.tags());
    }

    @Test
    void shouldThrowExceptionWhenTitleIsNull() {
        assertThrows(InvalidProductException.class, () -> new Product(
            Id.generate(),
            null,
            Slug.fromString("sample-product"),
            "Description",
            Set.of("cat-1"),
            true,
            new Price(100.0),
            new HashSet<>(),
            new Quantity(10),
            Set.of("image1.png"),
            Set.of("tag1")
        ));
    }

    @Test
    void shouldUpdateProductCorrectlyWithSameAttributeIds() {
        Id attrId = Id.generate();
        Id defId = Id.generate();
        ProductAttributeValue pav = new ProductAttributeValue(attrId, defId, "val", null, null, null);
        
        Product product = new Product(
            Id.generate(),
            "Sample Product",
            Slug.fromString("sample-product"),
            "Description",
            Set.of("cat-1"),
            true,
            new Price(100.0),
            Set.of(pav),
            new Quantity(10),
            Set.of("image1.png"),
            Set.of("tag1")
        );

        ProductAttributeValue updatedPav = new ProductAttributeValue(attrId, defId, "new-val", null, null, null);
        
        product.update(
            "New Title",
            Slug.fromString("new-slug"),
            "New Description",
            Set.of("cat-2"),
            false,
            new Price(150.0),
            new Quantity(5),
            Set.of("image2.png"),
            Set.of(updatedPav),
            Set.of("tag2")
        );

        assertEquals("New Title", product.title());
        assertEquals("new-slug", product.slug().value());
        assertEquals("New Description", product.description());
        assertEquals(Set.of("cat-2"), product.categories());
        assertFalse(product.isActive());
        assertEquals(150.0, product.price().value());
        assertEquals(5, product.stock().value());
        assertEquals(Set.of("image2.png"), product.images());
        assertEquals(Set.of("tag2"), product.tags());
        assertEquals(1, product.attributeValues().size());
        assertEquals("new-val", product.attributeValues().iterator().next().string_value());
    }

    @Test
    void shouldThrowExceptionOnUpdateWhenAttributeIdsDoNotMatch() {
        Id attrId = Id.generate();
        Id defId = Id.generate();
        ProductAttributeValue pav = new ProductAttributeValue(attrId, defId, "val", null, null, null);
        
        Product product = new Product(
            Id.generate(),
            "Sample Product",
            Slug.fromString("sample-product"),
            "Description",
            Set.of("cat-1"),
            true,
            new Price(100.0),
            Set.of(pav),
            new Quantity(10),
            Set.of("image1.png"),
            Set.of("tag1")
        );

        // Intento actualizar usando un ID de atributo diferente
        ProductAttributeValue differentPav = new ProductAttributeValue(Id.generate(), defId, "new-val", null, null, null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> product.update(
            "New Title",
            Slug.fromString("new-slug"),
            "New Description",
            Set.of("cat-1"),
            true,
            new Price(150.0),
            new Quantity(5),
            Set.of("image2.png"),
            Set.of(differentPav),
            Set.of("tag2")
        ));
        assertTrue(exception.getMessage().contains("not found in current product attribute values"));
    }

    @Test
    void shouldValidateGlobalAndCategoryAttributesSuccessfully() {
        Id globalDefId = Id.generate();
        AttributeDefinition globalDef = new AttributeDefinition(globalDefId, "Global", Slug.fromString("global"), DataType.STRING, true);
        ProductAttributeValue globalPav = new ProductAttributeValue(Id.generate(), globalDefId, "global-value", null, null, null);

        Id catDefId = Id.generate();
        AttributeDefinition catDef = new AttributeDefinition(catDefId, "CatAttr", Slug.fromString("cat-attr"), DataType.INTEGER, false);
        ProductAttributeValue catPav = new ProductAttributeValue(Id.generate(), catDefId, null, 123, null, null);

        // Agregamos ambos atributos al producto
        Product product = new Product(
            Id.generate(),
            "Title",
            Slug.fromString("slug"),
            "Desc",
            Set.of("cat-1"),
            true,
            new Price(10.0),
            Set.of(globalPav, catPav),
            new Quantity(5),
            new HashSet<>(),
            new HashSet<>()
        );

        CategoryAttribute categoryAttribute = new CategoryAttribute(Id.generate(), catDefId, true, true, true);
        categoryAttribute.load_attribute_definition(catDef);

        assertDoesNotThrow(() -> product.validGlobalAttributesAndCategoryAttributes(
            Set.of(globalDef),
            Set.of(categoryAttribute)
        ));
    }

    @Test
    void shouldThrowExceptionWhenRequiredGlobalAttributeIsMissing() {
        Id globalDefId = Id.generate();
        AttributeDefinition globalDef = new AttributeDefinition(globalDefId, "Global", Slug.fromString("global"), DataType.STRING, true);

        // El producto no tiene atributos definidos
        Product product = createDefaultProduct();

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> product.validGlobalAttributesAndCategoryAttributes(
            Set.of(globalDef),
            new HashSet<>()
        ));
        assertTrue(exception.getMessage().contains("this is a global attribute definition"));
    }

    @Test
    void shouldThrowExceptionWhenRequiredCategoryAttributeIsMissing() {
        Id catDefId = Id.generate();
        AttributeDefinition catDef = new AttributeDefinition(catDefId, "CatAttr", Slug.fromString("cat-attr"), DataType.INTEGER, false);

        Product product = createDefaultProduct();

        CategoryAttribute categoryAttribute = new CategoryAttribute(Id.generate(), catDefId, true, true, true);
        categoryAttribute.load_attribute_definition(catDef);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> product.validGlobalAttributesAndCategoryAttributes(
            new HashSet<>(),
            Set.of(categoryAttribute)
        ));
        assertTrue(exception.getMessage().contains("The product attribute is missing for the attribute definition"));
    }

    @Test
    void shouldThrowExceptionWhenProductHasExtraAttributesNotConfigured() {
        Id extraDefId = Id.generate();
        ProductAttributeValue extraPav = new ProductAttributeValue(Id.generate(), extraDefId, "extra", null, null, null);

        Product product = new Product(
            Id.generate(),
            "Title",
            Slug.fromString("slug"),
            "Desc",
            Set.of("cat-1"),
            true,
            new Price(10.0),
            Set.of(extraPav),
            new Quantity(5),
            new HashSet<>(),
            new HashSet<>()
        );

        RuntimeException exception = assertThrows(RuntimeException.class, () -> product.validGlobalAttributesAndCategoryAttributes(
            new HashSet<>(),
            new HashSet<>()
        ));
        assertTrue(exception.getMessage().contains("do not defined by the categories"));
    }

    @Test
    void shouldAddProductAttributeSuccessfullyWhenPermitted() {
        Product product = createDefaultProduct();
        Me me = new Me(Id.generate(), Set.of(Permission.updateProduct()));

        Id defId = Id.generate();
        ProductAttributeValue pav = new ProductAttributeValue(Id.generate(), defId, "value", null, null, null);

        assertDoesNotThrow(() -> product.addProductAttribute(me, pav));
        assertEquals(1, product.attributeValues().size());
    }

    @Test
    void shouldThrowExceptionOnAddAttributeWhenNoPermission() {
        Product product = createDefaultProduct();
        Me me = new Me(Id.generate(), Set.of(Permission.createProduct())); // Diferente permiso

        ProductAttributeValue pav = new ProductAttributeValue(Id.generate(), Id.generate(), "value", null, null, null);

        assertThrows(UnauthorizedException.class, () -> product.addProductAttribute(me, pav));
    }

    @Test
    void shouldThrowExceptionOnAddAttributeWhenAlreadyExists() {
        Id defId = Id.generate();
        ProductAttributeValue pav1 = new ProductAttributeValue(Id.generate(), defId, "value1", null, null, null);
        
        Product product = new Product(
            Id.generate(), "Title", Slug.fromString("slug"), "Desc", Set.of("cat-1"), true,
            new Price(10.0), Set.of(pav1), new Quantity(5), new HashSet<>(), new HashSet<>()
        );

        Me me = new Me(Id.generate(), Set.of(Permission.updateProduct()));
        ProductAttributeValue pav2 = new ProductAttributeValue(Id.generate(), defId, "value2", null, null, null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> product.addProductAttribute(me, pav2));
        assertEquals("An product attribute with the same 'attribute definition id' already exists.", exception.getMessage());
    }

    @Test
    void shouldRemoveAttributeSuccessfullyWhenPermittedAndNotRequired() {
        Id attrId = Id.generate();
        Id defId = Id.generate();
        ProductAttributeValue pav = new ProductAttributeValue(attrId, defId, "value", null, null, null);
        
        Product product = new Product(
            Id.generate(), "Title", Slug.fromString("slug"), "Desc", Set.of("cat-1"), true,
            new Price(10.0), Set.of(pav), new Quantity(5), new HashSet<>(), new HashSet<>()
        );

        Me me = new Me(Id.generate(), Set.of(Permission.updateProduct()));

        assertDoesNotThrow(() -> product.removeAttribute(me, attrId, null));
        assertTrue(product.attributeValues().isEmpty());
    }

    @Test
    void shouldThrowExceptionOnRemoveAttributeWhenNotExists() {
        Product product = createDefaultProduct();
        Me me = new Me(Id.generate(), Set.of(Permission.updateProduct()));

        assertThrows(DataNotFound.class, () -> product.removeAttribute(me, Id.generate(), null));
    }

    @Test
    void shouldThrowExceptionOnRemoveAttributeWhenRequiredByCategory() {
        Id attrId = Id.generate();
        Id defId = Id.generate();
        ProductAttributeValue pav = new ProductAttributeValue(attrId, defId, "value", null, null, null);
        
        Product product = new Product(
            Id.generate(), "Title", Slug.fromString("slug"), "Desc", Set.of("cat-1"), true,
            new Price(10.0), Set.of(pav), new Quantity(5), new HashSet<>(), new HashSet<>()
        );

        Me me = new Me(Id.generate(), Set.of(Permission.updateProduct()));
        CategoryAttribute categoryAttribute = new CategoryAttribute(Id.generate(), defId, true, true, true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> product.removeAttribute(me, attrId, categoryAttribute));
        assertTrue(exception.getMessage().contains("cannot be removed"));
    }
}
