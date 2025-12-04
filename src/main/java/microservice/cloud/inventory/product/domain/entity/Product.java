package microservice.cloud.inventory.product.domain.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import microservice.cloud.inventory.attribute.domain.entity.AttributeDefinition;
import microservice.cloud.inventory.product.domain.event.ProductCreatedEvent;
import microservice.cloud.inventory.product.domain.event.ProductDeletedEvent;
import microservice.cloud.inventory.product.domain.event.ProductUpdatedEvent;
import microservice.cloud.inventory.product.domain.exception.InvalidProductException;
import microservice.cloud.inventory.product.domain.value_objects.Price;
import microservice.cloud.inventory.product.domain.value_objects.Quantity;
import microservice.cloud.inventory.shared.domain.entity.AggregateRoot;
import microservice.cloud.inventory.shared.domain.exception.DataNotFound;
import microservice.cloud.inventory.shared.domain.value_objects.Id;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;

public class Product extends AggregateRoot{
    private Id id;
    private String title;
    private Slug slug;
    private String description;
    
    private List<String> categories;
    
    private Price price;
    private Quantity stock;
    private List<String> images;
    private Map<String, ProductAttributeValue> attributeValues = new HashMap<>();

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
            throw new InvalidProductException("Products must have at least one category");

        if(title == null)
            throw new InvalidProductException("Products must have at least one category");

        this.id = id;
        this.title = title;
        this.slug = slug;
        this.description = description;
        this.categories = categories;
        attributeValues.stream().forEach(attr -> this.attributeValues.put(attr.id().value(), attr));
        this.price = price;
        this.stock = stock;
        this.images = images;

        this.dispatch(new ProductCreatedEvent(
                id.value(), 
                title, 
                slug.value(), 
                description, 
                categories, 
                price.value(), 
                stock.value(), 
                images, 
                this.attributeValues()
            )
        );
    }

    public void validAttributes(List<AttributeDefinition> attrs) {
        Map<String, ProductAttributeValue> attributeDefinitionPerProductAttributeValue = new HashMap<>();

        this.attributeValues().stream().forEach(attr -> {
            attributeDefinitionPerProductAttributeValue.put(attr.attribute_definition().value(), attr);
        });

        attrs.stream().forEach(a -> {
            ProductAttributeValue attr = attributeDefinitionPerProductAttributeValue
                .get(a.id().value());

            if(attr == null)
                throw new RuntimeException(
                        "undeclared attribute for the definition attribute: " 
                        + a.id().value()
                    );

            attr.validTypes(a);
        });
    }

    public void addProductAttribute(ProductAttributeValue attr) {
        attributeValues.values().stream().forEach(a -> {
            if(a.attribute_definition().equals(attr.attribute_definition()))
                throw new RuntimeException("An attribute with the same attribute definition already exists.");
        });

        attributeValues.put(attr.id().value(), attr);

        this.dispatch(new ProductUpdatedEvent(
                id.value(), 
                title, 
                slug.value(), 
                description, 
                categories, 
                price.value(), 
                stock.value(), 
                images, 
                this.attributeValues()
            )
        );
    }

    public void update(Product product) {
        List<ProductAttributeValue> newAttrs = product.attributeValues();

        Map<String, ProductAttributeValue> mapNewAttrs = new HashMap<>();

        newAttrs.stream().forEach(a -> {
            mapNewAttrs.put(a.id().value(), a);
        });

        attributeValues.values().stream().forEach(a -> {
            ProductAttributeValue attr = mapNewAttrs.get(a.id().value());

            if(attr == null)
                throw new RuntimeException("you need to thicken the attribute " + a.id().value());
        });

        this.title = product.title();
        this.description = product.description();
        this.slug = product.slug();
        this.categories = product.categories();
        this.price = product.price();
        this.stock = product.stock();
        this.images = product.images();

        this.dispatch(new ProductUpdatedEvent(
                id.value(), 
                title, 
                slug.value(), 
                description, 
                categories, 
                price.value(), 
                stock.value(), 
                images, 
                this.attributeValues()
            )
        );
    }

    public void removeAttribute(Id productAttributeId) {
        if(attributeValues.get(productAttributeId.value()) == null)
            throw new DataNotFound("Product attribute not found");

        attributeValues.remove(productAttributeId.value());

        this.dispatch(new ProductUpdatedEvent(
                id.value(), 
                title, 
                slug.value(), 
                description, 
                categories, 
                price.value(), 
                stock.value(), 
                images, 
                this.attributeValues()
            )
        );
    }

    public void delete() {
        this.dispatch(new ProductDeletedEvent(id.value()));
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
        return new ArrayList<>(attributeValues.values());
    }

    public Quantity stock() {
        return stock;
    }

    public List<String> images() {
        return images;
    }
}
