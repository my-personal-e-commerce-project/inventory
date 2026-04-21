package microservice.cloud.inventory.attribute.domain.entity;

import microservice.cloud.inventory.attribute.domain.event.CreatedGlobalAttributeDefinition;
import microservice.cloud.inventory.attribute.domain.value_objects.DataType;
import microservice.cloud.inventory.shared.domain.entity.AggregateRoot;
import microservice.cloud.inventory.shared.domain.value_objects.Id;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;

public class AttributeDefinition extends AggregateRoot {
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
        if(name == null)
            throw new RuntimeException("The name cannot be null");

        if(type == null)
            throw new RuntimeException("The type cannot be null");

        this.id = id;
        this.name = name;
        this.slug = slug;
        this.type = type;
        this.is_global = is_global;
    }

    public static AttributeDefinition factory(Id id, String name, Slug slug, DataType type, boolean is_global) {
        AttributeDefinition attrDef = new AttributeDefinition(id, name, slug, type, is_global);

        if(is_global) {
            attrDef.publishEvent(
                new CreatedGlobalAttributeDefinition(
                    id.value(), 
                    name, 
                    slug.value(), 
                    type.toString(), 
                    is_global
                )
            );
        }

        return attrDef;
    }

    public void update(String name, Slug slug, DataType type, boolean is_global) {
        if(is_global != this.is_global)
            throw new RuntimeException("Can you not change the value of is_global.");

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
