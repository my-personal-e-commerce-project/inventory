package microservice.cloud.inventory.category.domain.entity;

import java.util.List;
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
            throw new RuntimeException("The id cannot be null.");

        this.id = id;
        this.name = name;
        this.slug = slug;
        this.parent_id = parent_id;

        if(categoryAttributes != null)
            this.categoryAttributes = new HashSet<>(categoryAttributes);
    }

    private void validAddCategoryAttribute(CategoryAttribute attr) {
        if(attr.attribute_definition().is_global())
            throw new RuntimeException("The attribute definition cannot be global.");

        if (!this.categoryAttributes.add(attr))
            throw new RuntimeException("The category attribute with id: " 
                    + attr.id().value() 
                    +  " or attribute definition id: " 
                    + attr.attribute_definition_id().value() 
                    + " already exists.");
    }

    public static Category factory(
        Me me,
        Id id,
        String name, 
        Slug slug, 
        Id parent_id,
        List<CategoryAttribute> attributes
    ) {
        if(me == null)
            throw new RuntimeException("You do not have permission to perform this action.");

        me.IHavePermission(Permission.createCategory());

        Category category = new Category(id, name, slug, parent_id, null);

        attributes.forEach(attr -> {
            category.validAddCategoryAttribute(attr);
        });

        return category;
    }

    public void addCategoryAttribute(
        Me me,
        CategoryAttribute attr
    ) {
        if(me == null)
            throw new RuntimeException("You do not have permission to perform this action.");

        me.IHavePermission(Permission.updateCategory());
     
        validAddCategoryAttribute(attr);
    }

    public void update(
        Me me, 
        String name, 
        Slug slug, 
        Id parent_id, 
        Set<CategoryAttribute> categoryAttributes
    ) {
        if(me == null)
            throw new RuntimeException("You do not have permission to perform this action.");

        me.IHavePermission(Permission.updateCategory());

        this.categoryAttributes.stream().forEach(attr -> {
            if(
                categoryAttributes
                    .stream()
                    .filter(actualAttr -> actualAttr.id().equals(attr.id()))
                    .findFirst()
                    .isEmpty()
            )
                throw new RuntimeException("Category attribute with id: '" 
                    + attr.id().value() 
                    + "' not found in the new category attributes.");

        });

        this.categoryAttributes = categoryAttributes;
        this.name = name;
        this.slug = slug;
        this.parent_id = parent_id;
    }

    public void removeCategoryAttribute(Me me, Id id) {
        if(me == null)
            throw new RuntimeException("You do not have permission to perform this action.");

        me.IHavePermission(Permission.updateCategory());

        if(id == null)
            throw new RuntimeException("Id can not be null.");

        boolean removed = this.categoryAttributes.removeIf(attr -> attr.id().equals(id));
    
        if(!removed) throw new DataNotFound("Category attribute not found.");
    }
   
    public void canIDeleteThisCategory(Me me) {
        if(me == null)
            throw new RuntimeException("You do not have permission to perform this action.");

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
