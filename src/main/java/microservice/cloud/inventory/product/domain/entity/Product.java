package microservice.cloud.inventory.product.domain.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

public class Product {
    private Id id;
    private String title;
    private Id category_id;
    private Double price;
    
    private List<ProductAttributeValue> attributeValues;

    public Product(Id id, String title, Id category_id, Double price, List<ProductAttributeValue> attributeValues) {
        this.id = id;
        this.title = title;
        this.category_id = category_id;
        this.price = price;
        this.attributeValues = attributeValues;
    }

    public Id id() {
        return id;
    }

    public String title() {
        return title;
    }

    public Id category_id() {
        return category_id;
    }

    public Double price() {
        return price;
    }

    public List<ProductAttributeValue> attributeValues() {
        return attributeValues;
    }
}
