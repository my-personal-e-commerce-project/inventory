package microservice.cloud.inventory.attribute.domain.entity;

import microservice.cloud.inventory.attribute.domain.value_objects.DataType;
import microservice.cloud.inventory.shared.domain.value_objects.Id;

public class AttributeDefinition {
    private Id id;
    private String name;
    private String slug;
    private DataType type;
    private boolean is_global;

    public AttributeDefinition(
        Id id, 
        String name, 
        String slug, 
        DataType type, 
        boolean is_global
    ) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.type = type;
        this.is_global = is_global;
    }

    public Id id() {
        return id;
    }

    public String name() {
        return name;
    }

    public String slug() {
        return slug;
    }

    public DataType type() {
        return type;
    }

    public boolean isGlobal() {
        return is_global;
    }
}
