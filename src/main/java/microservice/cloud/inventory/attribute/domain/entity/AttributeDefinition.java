package microservice.cloud.inventory.attribute.domain.entity;

import microservice.cloud.inventory.attribute.domain.value_objects.DataType;
import microservice.cloud.inventory.shared.domain.value_objects.Id;
import microservice.cloud.inventory.shared.domain.value_objects.Me;
import microservice.cloud.inventory.shared.domain.value_objects.Permission;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;

public class AttributeDefinition {
    private Id id;
    private String name;
    private Slug slug;
    private DataType type;
    private boolean is_global;

    public AttributeDefinition(
        Id id, 
        String name, 
        Slug slug, 
        DataType type, 
        boolean is_global
    ) {
        if(id == null)
            throw new RuntimeException("The id cannot be null");

        if(name == null)
            throw new RuntimeException("The name cannot be null");

        if(slug == null)
            throw new RuntimeException("The slug cannot be null");

        if(type == null)
            throw new RuntimeException("The type cannot be null");

        this.id = id;
        this.name = name;
        this.slug = slug;
        this.type = type;
        this.is_global = is_global;
    }

    public void create(Me me) {

        if(me == null)
            throw new RuntimeException("You do not have permission to perform this action");

        me.IHavePermission(Permission.createAttributeDefinition());
    }

    public void update(Me me) {
        if(me == null)
            throw new RuntimeException("You do not have permission to perform this action");

        me.IHavePermission(Permission.updateAttributeDefinition());
    }

    public static void delete(Me me) {
        if(me == null)
            throw new RuntimeException("You do not have permission to perform this action");

        me.IHavePermission(Permission.deleteAttributeDefinition());
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

    public DataType type() {
        return type;
    }

    public boolean is_global() {
        return is_global;
    }
}
