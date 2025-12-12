package microservice.cloud.inventory.category.domain.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import microservice.cloud.inventory.category.domain.event.CategoryCreatedEvent;
import microservice.cloud.inventory.category.domain.event.CategoryDeletedEvent;
import microservice.cloud.inventory.category.domain.event.CategoryUpdatedEvent;
import microservice.cloud.inventory.shared.domain.entity.AggregateRoot;
import microservice.cloud.inventory.shared.domain.exception.DataNotFound;
import microservice.cloud.inventory.shared.domain.value_objects.Id;
import microservice.cloud.inventory.shared.domain.value_objects.Me;
import microservice.cloud.inventory.shared.domain.value_objects.Permission;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;

public class Category extends AggregateRoot {
    
    private Id id;
    private String name;
    private Slug slug;
    private Id parent_id;
    private Map<String, CategoryAttribute> categoryAttributes;

    public Category(Id id, String name, Slug slug, Id parent_id, List<CategoryAttribute> categoryAttributes) {
        
        if(id == null)
            throw new RuntimeException("The id cannot be null");

        this.id = id;
        this.name = name;
        this.slug = slug;
        this.parent_id = parent_id;

        this.categoryAttributes = new HashMap<>();
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

    public static Category factory(Me me, String name, Slug slug, Id parent_id, List<CategoryAttribute> categoryAttributes) {
        if(me == null)
            throw new RuntimeException("You must be authenticated to do this action");

        me.IHavePermission(Permission.createCategory());

        Category category = new Category(Id.generate(), name, slug, parent_id, categoryAttributes);

        category.dispatch(
            new CategoryCreatedEvent(
                category.id.value(), 
                name, 
                slug.value(), 
                parent_id == null? null: parent_id.value(), 
                categoryAttributes
            )
        );

        return category;
    }

    public void addCategoryAttribute(
        Me me,
        CategoryAttribute attr
    ) {
        if(me == null)
            throw new RuntimeException("You must be authenticated to do this action");

        me.IHavePermission(Permission.updateProduct());

        if(attr.attribute_definition().is_global() == true)
            throw new 
                RuntimeException(
                    "The definition of the attribute cannot be global"
                );
        
        this.categoryAttributes.put(attr.id().value(), attr);

        this.dispatch(
            new CategoryUpdatedEvent(
                id.value(), 
                name, 
                slug.value(), 
                parent_id.value(),
                categoryAttributes()
            )
        );
    }

    public void update(Me me, String name, Slug slug, Id parent_id, List<CategoryAttribute> categoryAttributes) {
        if(me == null)
            throw new RuntimeException("You must be authenticated to do this action");

        me.IHavePermission(Permission.updateProduct());

        categoryAttributes().stream().forEach(attr -> {
            if(this.categoryAttributes.get(attr.id().value())==null)
                throw new DataNotFound("Category attribute " + attr.id().value() + " not found");

            CategoryAttribute attribute = this.categoryAttributes.get(attr.id().value());

            if(!attr.attribute_definition().id().equals(attribute.attribute_definition().id())) {
                throw new RuntimeException("The id of your attribute definition does not match: " + attribute.attribute_definition().id().value());
            }
        });

        Map<String, CategoryAttribute> attrsMap = new HashMap<>();

        categoryAttributes().forEach(attr -> {
            attrsMap.put(attr.id().value(), attr);
        });

        this.categoryAttributes = attrsMap;
        this.name = name;
        this.slug = slug;
        this.parent_id = parent_id;

        this.dispatch(
            new CategoryUpdatedEvent(
                id.value(), 
                name, 
                slug.value(), 
                parent_id.value(),
                categoryAttributes()
            )
        );
    }

    public void removeCategoryAttribute(Me me, Id id) {
        if(me == null)
            throw new RuntimeException("You must be authenticated to do this action");

        me.IHavePermission(Permission.updateProduct());

        if(id == null)
            throw new RuntimeException("Id can not be null");

        CategoryAttribute data = this.categoryAttributes.get(
            id.value()
        );

        if(data == null)
            throw new DataNotFound("Category attribute not found");

        this.categoryAttributes.remove(
            id.value()
        );

        this.dispatch(
            new CategoryUpdatedEvent(
                id.value(), 
                name, 
                slug.value(), 
                parent_id.value(),
                categoryAttributes()
            )
        );
    }
   
    public void delete(Me me) {
        if(me == null)
            throw new RuntimeException("You must be authenticated to do this action");

        me.IHavePermission(Permission.updateProduct());

        this.dispatch(
            new CategoryDeletedEvent(
                id.value()
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

    public Id parent_id() {
        return parent_id;
    }

    public List<CategoryAttribute> categoryAttributes() {
        if(categoryAttributes == null)
            return null;
        return List.copyOf(categoryAttributes.values());
    }
}
