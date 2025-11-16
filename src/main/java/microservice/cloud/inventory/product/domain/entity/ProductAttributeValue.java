package microservice.cloud.inventory.product.domain.entity;

import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

public class ProductAttributeValue {
    private Id id;
    private Id product_id;
    private Id attribute_definition_id;

    private String string_value;
    private Integer integer_value; 
    private Double double_value;
    private Boolean boolean_value;

    public ProductAttributeValue(Id id, Id product_id, Id attribute_definition_id, String string_value, Integer integer_value, Double double_value, Boolean boolean_value) {
        this.id = id;
        this.product_id = product_id;
        this.attribute_definition_id = attribute_definition_id;
        this.string_value = string_value;
        this.integer_value = integer_value;
        this.double_value = double_value;
        this.boolean_value = boolean_value;
    }

    public Id id() {
        return id;
    }

    public Id product_id() {
        return product_id;
    }

    public Id attribute_definition_id() {
        return attribute_definition_id;
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
