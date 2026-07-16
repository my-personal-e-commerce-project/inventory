package microservice.cloud.inventory.category.domain.entity;

import java.util.HashSet;
import java.util.Set;

import microservice.cloud.inventory.category.domain.event.DeletedCategory;
import microservice.cloud.inventory.category.domain.value_objects.Status;
import microservice.cloud.inventory.shared.domain.entity.AggregateRoot;
import microservice.cloud.inventory.shared.domain.exception.DataNotFound;
import microservice.cloud.inventory.shared.domain.value_objects.Id;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;

public class Category extends AggregateRoot {
    private Id id;
    private String name;
    private Slug slug;
    private Id parent_id;
    private Status status;
    private Set<CategoryAttribute> categoryAttributes = new HashSet<>();

    public Category(Id id, String name, Slug slug, Id parent_id, Status status, Set<CategoryAttribute> categoryAttributes) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.parent_id = parent_id;
        this.status = status;

        if(categoryAttributes != null)
            this.categoryAttributes = new HashSet<>(categoryAttributes);
    }

    public void update(
        String name, 
        Slug slug, 
        Id parent_id, 
        Set<CategoryAttribute> categoryAttributes
    ) {
        this.categoryAttributes.stream().forEach(attr -> {
            if(
                categoryAttributes
                    .stream()
                    .filter(updateAttr -> updateAttr.id().equals(attr.id()))
                    .findFirst()
                    .isEmpty()
            )
                throw new RuntimeException("Category attribute with id: '" 
                    + attr.id().value() 
                    + "' not found in your new list of category attributes.");

        });

        categoryAttributes.stream().forEach(newAttr -> {
            CategoryAttribute oldAttr = this.categoryAttributes.stream()
                .filter(currentAttr -> currentAttr.id().equals(newAttr.id()))
                .findFirst()
                .orElse(null);

            if(
                oldAttr == null
            ) {
                throw new RuntimeException("'Category attribute' with id: '" 
                    + newAttr.id().value() 
                    + "' not found in the current list of category attributes.");
            }
        });

        this.categoryAttributes = categoryAttributes;
        this.name = name;
        this.slug = slug;
        this.parent_id = parent_id;
    }

    public void validAddCategoryAttribute(CategoryAttribute attr) {
        if(attr.attribute_definition().is_global())
            throw new RuntimeException("The 'attribute definition' cannot be global.");
    }

    public void addCategoryAttribute(
        CategoryAttribute attr
    ) {
            
        validAddCategoryAttribute(attr);

        if (!this.categoryAttributes.add(attr))
            throw new RuntimeException("The 'category attribute' with 'attribute definition id': '" 
                    + attr.attribute_definition_id().value() 
                    + "' already exists.");
    }
    
    public void removeCategoryAttribute(Id id) {
        if(id == null)
            throw new RuntimeException("Id can not be null.");

        CategoryAttribute catAttr = this.categoryAttributes.stream()
            .filter(currentAttr -> currentAttr.id().equals(id))
            .findFirst()
            .orElse(null);

        if(catAttr == null) throw new DataNotFound("'Category attribute' not found.");

        this.categoryAttributes
            .removeIf(attr -> attr.id().equals(id));
    }

    public void enabledCategory() {
        this.status = Status.ENABLED;
    }

    public void deleteCategory() {
        this.status = Status.DISABLED;

        this.publishEvent(
            new DeletedCategory(
                this.id.value()
            )
        );
    }

    public Id id() {
        return id;
    }

    public String name() {
        return name;
    }

    public Slug slug() {
        return slug;
    }

    public Status status() {
        return status;
    }

    public Id parent_id() {
        return parent_id;
    }

    public Set<CategoryAttribute> categoryAttributes() {
        return new HashSet<>(categoryAttributes);
    }
}
