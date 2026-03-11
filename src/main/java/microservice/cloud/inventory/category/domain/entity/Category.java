package microservice.cloud.inventory.category.domain.entity;

import java.util.HashSet;
import java.util.Set;

import microservice.cloud.inventory.shared.domain.exception.DataNotFound;
import microservice.cloud.inventory.shared.domain.value_objects.Id;
import microservice.cloud.inventory.shared.domain.value_objects.Me;
import microservice.cloud.inventory.shared.domain.value_objects.Permission;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;

public class Category {
    private Id id;
    private String name;
    private Slug slug;
    private Id parent_id;
    private Set<CategoryAttribute> categoryAttributes = new HashSet<>();

    public Category(Id id, String name, Slug slug, Id parent_id, Set<CategoryAttribute> categoryAttributes) {
        
        if(id == null)
            throw new RuntimeException("The id cannot be null");

        this.id = id;
        this.name = name;
        this.slug = slug;
        this.parent_id = parent_id;

        this.categoryAttributes = new HashSet<>(categoryAttributes);
    }

    public void create(Me me) {
        if(me == null)
            throw new RuntimeException("You do not have permission to perform this action");

        me.IHavePermission(Permission.createCategory());

        categoryAttributes().stream().forEach((attr) -> {
            validCategoryAttribute(attr);
        });
    }

    private void validCategoryAttribute(CategoryAttribute categoryAttribute) {

        if(categoryAttribute.attribute_definition().is_global() == true)
            throw new 
                RuntimeException(
                    "The definition of the attribute cannot be global"
                );
       
        if (!categoryAttribute.attribute_definition().slug().value().startsWith(this.slug().value()+":"))
            throw new RuntimeException(String.format(
                "Invalid attribute namespace: The attribute slug '%s' must be prefixed with the category slug '%s:'",
                categoryAttribute.attribute_definition().slug().value(),
                this.slug().value()
            ));
    }

    public void addCategoryAttribute(
        Me me,
        CategoryAttribute attr
    ) {
        if(me == null)
            throw new RuntimeException("You do not have permission to perform this action");

        me.IHavePermission(Permission.updateCategory());
        
        this.validCategoryAttribute(attr);
        this.categoryAttributes.add(attr);
    }

    public void update(Me me, String name, Slug slug, Id parent_id, Set<CategoryAttribute> categoryAttributes) {
        if(me == null)
            throw new RuntimeException("You do not have permission to perform this action");

        me.IHavePermission(Permission.updateCategory());

        categoryAttributes.stream().forEach(attr -> {
            if(this.categoryAttributes.contains(attr))
                throw new DataNotFound("Category attribute " + attr.id().value() + " not found");

            this.validCategoryAttribute(attr);
        });

        this.categoryAttributes = categoryAttributes;
        this.name = name;
        this.slug = slug;
        this.parent_id = parent_id;
    }

    public void removeCategoryAttribute(Me me, Id id) {
        if(me == null)
            throw new RuntimeException("You do not have permission to perform this action");

        me.IHavePermission(Permission.updateCategory());

        if(id == null)
            throw new RuntimeException("Id can not be null");

        boolean removed = this.categoryAttributes.removeIf(attr -> attr.id().equals(id));
    
        if(!removed) throw new DataNotFound("Category attribute not found");
    }
   
    public void canIDeleteThisCategory(Me me) {
        if(me == null)
            throw new RuntimeException("You do not have permission to perform this action");

        me.IHavePermission(Permission.deleteCategory());
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

    public Set<CategoryAttribute> categoryAttributes() {
        return categoryAttributes;
    }
}
