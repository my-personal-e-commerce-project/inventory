package microservice.cloud.inventory.category.domain.entity;

import microservice.cloud.inventory.attribute.domain.entity.AttributeDefinition;
import microservice.cloud.inventory.shared.domain.value_objects.Id;

public class CategoryAttribute {
    private Id id;
    private AttributeDefinition attribute_definition;
    private Boolean is_required;
    private Boolean is_filterable;
    private Boolean is_sortable;

    public CategoryAttribute(
        Id id, 
        AttributeDefinition attribute_definition, 
        Boolean is_required, 
        Boolean is_filterable, 
        Boolean is_sortable
    ) {
        this.attribute_definition = attribute_definition;
        this.is_required = is_required;
        this.is_filterable = is_filterable;
        this.is_sortable = is_sortable;
    }

    public Id id() {
        return id;
    }

    public AttributeDefinition attribute_definition() {
        return attribute_definition;
    }

    public Boolean is_required() {
        return is_required;
    }

    public Boolean is_filterable() {
        return is_filterable;
    }

    public Boolean is_sortable() {
        return is_sortable;
    }
}
