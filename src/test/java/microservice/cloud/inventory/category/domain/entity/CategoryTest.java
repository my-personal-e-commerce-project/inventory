package microservice.cloud.inventory.category.domain.entity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import microservice.cloud.inventory.attribute.domain.entity.AttributeDefinition;
import microservice.cloud.inventory.attribute.domain.value_objects.DataType;
import microservice.cloud.inventory.category.domain.event.CreatedCategoryAttribute;
import microservice.cloud.inventory.category.domain.event.DeletedCategory;
import microservice.cloud.inventory.category.domain.value_objects.Status;
import microservice.cloud.inventory.shared.domain.event.DomainEvent;
import microservice.cloud.inventory.shared.domain.exception.DataNotFound;
import microservice.cloud.inventory.shared.domain.value_objects.Id;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CategoryTest {

    private AttributeDefinition createAttributeDefinition(boolean isGlobal) {
        return new AttributeDefinition(
            Id.generate(),
            "Attr Def",
            Slug.fromString("attr-def"),
            DataType.STRING,
            isGlobal
        );
    }

    @Test
    void shouldCreateCategorySuccessfully() {
        Id id = Id.generate();
        Slug slug = Slug.fromString("category-slug");
        Id parentId = Id.generate();
        Set<CategoryAttribute> attributes = new HashSet<>();

        Category category = new Category(id, "Category Name", slug, parentId, Status.ENABLED, attributes);

        assertEquals(id, category.id());
        assertEquals("Category Name", category.name());
        assertEquals(slug, category.slug());
        assertEquals(parentId, category.parent_id());
        assertEquals(Status.ENABLED, category.status());
        assertTrue(category.categoryAttributes().isEmpty());
    }

    @Test
    void shouldUpdateCategorySuccessfully() {
        Id id = Id.generate();
        CategoryAttribute attr = new CategoryAttribute(Id.generate(), Id.generate(), true, true, true);
        Category category = new Category(id, "Category Name", Slug.fromString("slug"), null, Status.ENABLED, Set.of(attr));

        CategoryAttribute updatedAttr = new CategoryAttribute(attr.id(), attr.attribute_definition_id(), false, false, false);
        Slug newSlug = Slug.fromString("new-slug");
        Id parentId = Id.generate();

        category.update("New Name", newSlug, parentId, Set.of(updatedAttr));

        assertEquals("New Name", category.name());
        assertEquals(newSlug, category.slug());
        assertEquals(parentId, category.parent_id());
        assertEquals(1, category.categoryAttributes().size());
        CategoryAttribute actualAttr = category.categoryAttributes().iterator().next();
        assertFalse(actualAttr.is_required());
    }

    @Test
    void shouldThrowExceptionOnUpdateWhenExistingAttributeIsMissing() {
        Id id = Id.generate();
        CategoryAttribute attr1 = new CategoryAttribute(Id.generate(), Id.generate(), true, true, true);
        CategoryAttribute attr2 = new CategoryAttribute(Id.generate(), Id.generate(), true, true, true);
        Category category = new Category(id, "Category Name", Slug.fromString("slug"), null, Status.ENABLED, Set.of(attr1, attr2));

        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> category.update("New Name", Slug.fromString("new-slug"), null, Set.of(attr1))
        );
        assertTrue(exception.getMessage().contains("not found in your new list of category attributes"));
    }

    @Test
    void shouldThrowExceptionOnUpdateWhenNewAttributeIdDoesNotExistInCurrent() {
        Id id = Id.generate();
        CategoryAttribute attr1 = new CategoryAttribute(Id.generate(), Id.generate(), true, true, true);
        Category category = new Category(id, "Category Name", Slug.fromString("slug"), null, Status.ENABLED, Set.of(attr1));

        CategoryAttribute nonExistentAttr = new CategoryAttribute(Id.generate(), Id.generate(), true, true, true);

        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> category.update("New Name", Slug.fromString("new-slug"), null, Set.of(nonExistentAttr))
        );
        assertTrue(exception.getMessage().contains("not found in the current list of category attributes"));
    }

    @Test
    void shouldAddCategoryAttributeSuccessfullyAndPublishEventIfRequired() {
        Category category = new Category(Id.generate(), "Category", Slug.fromString("slug"), null, Status.ENABLED, new HashSet<>());
        CategoryAttribute attr = new CategoryAttribute(Id.generate(), Id.generate(), true, true, true);
        AttributeDefinition def = createAttributeDefinition(false);
        attr.load_attribute_definition(def);

        category.addCategoryAttribute(attr);

        assertEquals(1, category.categoryAttributes().size());
        assertTrue(category.categoryAttributes().contains(attr));

        List<DomainEvent> events = category.getEvents();
        assertEquals(1, events.size());
        assertTrue(events.get(0) instanceof CreatedCategoryAttribute);
        CreatedCategoryAttribute event = (CreatedCategoryAttribute) events.get(0);
        assertEquals(category.id().value(), event.category_id());
        assertEquals(attr.id().value(), event.category_attribute_id());
        assertEquals(attr.attribute_definition_id().value(), event.attribute_definition_id());
    }

    @Test
    void shouldAddCategoryAttributeSuccessfullyAndNotPublishEventIfNotRequired() {
        Category category = new Category(Id.generate(), "Category", Slug.fromString("slug"), null, Status.ENABLED, new HashSet<>());
        CategoryAttribute attr = new CategoryAttribute(Id.generate(), Id.generate(), false, true, true);
        AttributeDefinition def = createAttributeDefinition(false);
        attr.load_attribute_definition(def);

        category.addCategoryAttribute(attr);

        assertEquals(1, category.categoryAttributes().size());
        assertTrue(category.getEvents().isEmpty());
    }

    @Test
    void shouldThrowExceptionWhenAddingAttributeWithGlobalDefinition() {
        Category category = new Category(Id.generate(), "Category", Slug.fromString("slug"), null, Status.ENABLED, new HashSet<>());
        CategoryAttribute attr = new CategoryAttribute(Id.generate(), Id.generate(), true, true, true);
        AttributeDefinition def = createAttributeDefinition(true);
        attr.load_attribute_definition(def);

        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> category.addCategoryAttribute(attr)
        );
        assertEquals("The 'attribute definition' cannot be global.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenAddingDuplicateCategoryAttribute() {
        Id defId = Id.generate();
        CategoryAttribute attr1 = new CategoryAttribute(Id.generate(), defId, true, true, true);
        Category category = new Category(Id.generate(), "Category", Slug.fromString("slug"), null, Status.ENABLED, Set.of(attr1));

        CategoryAttribute attr2 = new CategoryAttribute(Id.generate(), defId, false, false, false);
        AttributeDefinition def = createAttributeDefinition(false);
        attr2.load_attribute_definition(def);

        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> category.addCategoryAttribute(attr2)
        );
        assertTrue(exception.getMessage().contains("already exists"));
    }

    @Test
    void shouldRemoveCategoryAttributeSuccessfully() {
        Id attrId = Id.generate();
        CategoryAttribute attr = new CategoryAttribute(attrId, Id.generate(), true, true, true);
        Category category = new Category(Id.generate(), "Category", Slug.fromString("slug"), null, Status.ENABLED, Set.of(attr));

        category.removeCategoryAttribute(attrId);

        assertTrue(category.categoryAttributes().isEmpty());
    }

    @Test
    void shouldThrowExceptionWhenRemovingAttributeWithNullId() {
        Category category = new Category(Id.generate(), "Category", Slug.fromString("slug"), null, Status.ENABLED, new HashSet<>());
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> category.removeCategoryAttribute(null)
        );
        assertEquals("Id can not be null.", exception.getMessage());
    }

    @Test
    void shouldThrowDataNotFoundWhenRemovingNonExistentAttribute() {
        Category category = new Category(Id.generate(), "Category", Slug.fromString("slug"), null, Status.ENABLED, new HashSet<>());
        assertThrows(
            DataNotFound.class,
            () -> category.removeCategoryAttribute(Id.generate())
        );
    }

    @Test
    void shouldEnableCategory() {
        Category category = new Category(Id.generate(), "Category", Slug.fromString("slug"), null, Status.DISABLED, new HashSet<>());
        category.enabledCategory();
        assertEquals(Status.ENABLED, category.status());
    }

    @Test
    void shouldDeleteCategoryAndPublishEvent() {
        Category category = new Category(Id.generate(), "Category", Slug.fromString("slug"), null, Status.ENABLED, new HashSet<>());
        category.deleteCategory();

        assertEquals(Status.DISABLED, category.status());
        List<DomainEvent> events = category.getEvents();
        assertEquals(1, events.size());
        assertTrue(events.get(0) instanceof DeletedCategory);
        DeletedCategory event = (DeletedCategory) events.get(0);
        assertEquals(category.id().value(), event.id());
    }
}
