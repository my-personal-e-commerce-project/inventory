package microservice.cloud.inventory.product.domain.entity;

import microservice.cloud.inventory.attribute.domain.entity.AttributeDefinition;
import microservice.cloud.inventory.attribute.domain.value_objects.DataType;
import microservice.cloud.inventory.shared.domain.value_objects.Id;

public class ProductAttributeValue {
    private Id id;
    private Id attribute_definition;

    private String string_value;
    private Integer integer_value; 
    private Double double_value;
    private Boolean boolean_value;

    public ProductAttributeValue(Id id, Id attribute_definition, String string_value, Integer integer_value, Double double_value, Boolean boolean_value) {

        this.id = id;
        this.attribute_definition = attribute_definition;
        this.string_value = string_value;
        this.integer_value = integer_value;
        this.double_value = double_value;
        this.boolean_value = boolean_value;
    }

    public void validTypes(AttributeDefinition attr) {
        DataType type = attr.type();

        if(type == DataType.STRING && string_value == null)
            throw new RuntimeException("String value cannot be null from in this attribute");

        if(type == DataType.DOUBLE && double_value == null)
            throw new RuntimeException("Double value cannot be null this in this attribute");

        if(type == DataType.INTEGER && integer_value == null)
            throw new RuntimeException("Integer value cannot be null in this attribute");

        if(type == DataType.BOOLEAN && boolean_value == null)
            throw new RuntimeException("Boolean value cannot be null in this attribute");
    }

    public Id id() {
        return id;
    }

    public Id attribute_definition() {
        return attribute_definition;
    }

    public String string_value() {
        return string_value;
    }

    public Integer integer_value() {
        return integer_value;
    }

    public Double double_value() {
        return double_value;
    }

    public Boolean boolean_value() {
        return boolean_value;
    }
}
