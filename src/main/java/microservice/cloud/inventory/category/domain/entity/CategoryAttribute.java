package microservice.cloud.inventory.category.domain.entity;

import java.util.Objects;

import microservice.cloud.inventory.attribute.domain.entity.AttributeDefinition;
import microservice.cloud.inventory.shared.domain.value_objects.Id;

public class CategoryAttribute {
    private Id id;
    private Id attribute_definition_id;
    private AttributeDefinition attribute_definition;
    private Boolean is_required;
    private Boolean is_filterable;
    private Boolean is_sortable;

    public CategoryAttribute(
        Id id, 
        Id attribute_definition_id, 
        Boolean is_required, 
        Boolean is_filterable, 
        Boolean is_sortable
    ) {
        if(id == null) {
            throw new RuntimeException("The id cannot be null");
        }

        this.id = id;
        this.attribute_definition_id = attribute_definition_id;
        this.is_required = is_required;
        this.is_filterable = is_filterable;
        this.is_sortable = is_sortable;
    }

    public void load_attribute_definition(AttributeDefinition attributeDefinition) {
        if(attributeDefinition.is_global()) {
            throw new RuntimeException("The attribute definition cannot be global attribute");
        }

        this.attribute_definition = attributeDefinition;
    }

    public Id id() {
        return id;
    }

    public Id attribute_definition_id() {
        return attribute_definition_id;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CategoryAttribute that = (CategoryAttribute) o;
        return Objects.equals(id, that.id) && Objects.equals(attribute_definition_id, that.attribute_definition_id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
