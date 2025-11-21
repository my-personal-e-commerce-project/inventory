package microservice.cloud.inventory.product.domain.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import microservice.cloud.inventory.product.domain.value_objects.Price;
import microservice.cloud.inventory.product.domain.value_objects.Quantity;

public class Product {
    private Id id;
    private String title;
    private String description;
    private Id category_id;
    private Price price;
    private Quantity stock;
    private List<String> images;
    private List<ProductAttributeValue> attributeValues;

    public Product(
        Id id, 
        String title,
        String description,
        Id category_id, 
        Price price, 
        List<ProductAttributeValue> attributeValues,
        Quantity stock,
        List<String> images
    ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category_id = category_id;
        this.price = price;
        this.attributeValues = attributeValues;
        this.stock = stock;
        this.images = images;
    }

    public Id id() {
        return id;
    }

    public String title() {
        return title;
    }

    public String description() {
        return description;
    }

    public Id category_id() {
        return category_id;
    }

    public Price price() {
        return price;
    }

    public List<ProductAttributeValue> attributeValues() {
        return attributeValues;
    }

    public Quantity stock() {
        return stock;
    }

    public List<String> images() {
        return images;
    }
}
