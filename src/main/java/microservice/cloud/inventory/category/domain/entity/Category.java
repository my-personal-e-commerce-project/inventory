package microservice.cloud.inventory.category.domain.entity;

import java.util.List;
import java.util.Map;

import microservice.cloud.inventory.shared.domain.value_objects.Id;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;

public class Category {
    
    private Id id;
    private String name;
    private Slug slug;
    private Id parent_id;
    private Map<String, CategoryAttribute> categoryAttributes;

    public Category(Id id, String name, Slug slug, Id parent_id, List<CategoryAttribute> categoryAttributes) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.parent_id = parent_id;

        if(categoryAttributes != null)
            categoryAttributes.stream()
                .forEach(
                   categoryAttribute -> {
                       this.categoryAttributes.put(
                           categoryAttribute.id().value(), 
                           categoryAttribute
                       );
                   }
               );
    }

    public void addCategoryAttribute(CategoryAttribute categoryAttribute) {
        this.categoryAttributes.put(
            categoryAttribute.id().value(), 
            categoryAttribute
        );
    }

    public void updateCategoryAttribute(CategoryAttribute categoryAttribute) {
        this.categoryAttributes.put(
            categoryAttribute.id().value(), 
            categoryAttribute
        );
    }

    public void removeCategoryAttribute(Id id) {
        this.categoryAttributes.remove(
            id().value()
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

    public Id parent_id() {
        return parent_id;
    }

    public List<CategoryAttribute> categoryAttributes() {
        return List.copyOf(categoryAttributes.values());
    }
}
