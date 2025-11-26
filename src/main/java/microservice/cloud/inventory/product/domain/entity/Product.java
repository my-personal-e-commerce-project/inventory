package microservice.cloud.inventory.product.domain.entity;

import java.util.List;

import microservice.cloud.inventory.product.domain.value_objects.Price;
import microservice.cloud.inventory.product.domain.value_objects.Quantity;
import microservice.cloud.inventory.shared.domain.value_objects.Id;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;

public class Product {
    private Id id;
    private String title;
    private Slug slug;
    private String description;
    
    private List<String> categories;
    
    private Price price;
    private Quantity stock;
    private List<String> images;
    private List<ProductAttributeValue> attributeValues;

    public Product(
        Id id, 
        String title,
        Slug slug,
        String description,
        List<String> categories, 
        Price price, 
        List<ProductAttributeValue> attributeValues,
        Quantity stock,
        List<String> images
    ) {

        if(categories == null || categories.size() < 1)
            throw new RuntimeException("Products must have at least one category");


        if(attributeValues == null || attributeValues.size() < 1)
            throw new RuntimeException("Products must have at least one attribute");

        this.id = id;
        this.title = title;
        this.slug = slug;
        this.description = description;
        this.categories = categories;
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

    public Slug slug() {
        return slug;
    }

    public String description() {
        return description;
    }

    public List<String> categories() {
        return categories;
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
