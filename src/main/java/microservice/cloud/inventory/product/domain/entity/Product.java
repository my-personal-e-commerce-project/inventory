package microservice.cloud.inventory.product.domain.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import microservice.cloud.inventory.category.domain.entity.CategoryAttribute;
import microservice.cloud.inventory.product.domain.exception.InvalidProductException;
import microservice.cloud.inventory.product.domain.value_objects.Price;
import microservice.cloud.inventory.product.domain.value_objects.Quantity;
import microservice.cloud.inventory.shared.domain.entity.AggregateRoot;
import microservice.cloud.inventory.shared.domain.exception.DataNotFound;
import microservice.cloud.inventory.shared.domain.value_objects.Id;
import microservice.cloud.inventory.shared.domain.value_objects.Me;
import microservice.cloud.inventory.shared.domain.value_objects.Permission;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;

public class Product extends AggregateRoot{
    private Id id;
    private String title;
    private Slug slug;
    private String description;
    private List<String> tags;
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
        List<String> images,
        List<String> tags
    ) {
        if(id == null)
            throw new RuntimeException("The id cannot be null");

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
        this.tags = tags;
    }

    public static Product create(
            Me me,
            Id id,
            String title,
            Slug slug,
            String description,
            List<String> categories,
            Price price,
            Quantity stock,
            List<String> images,
            List<ProductAttributeValue> attributeValues,
            List<String> tags
    ) {
        if(me == null)
            throw new RuntimeException("You must be authenticated to do this action");

        me.IHavePermission(Permission.createProduct());

        Product product = new Product(
            id, 
            title, 
            slug, 
            description, 
            categories, 
            price, 
            attributeValues, 
            stock, 
            images, 
            tags
        );

        return product;
    }

    public void validAttributes(List<CategoryAttribute> attrs) {

        Map<String, ProductAttributeValue> productByDefinitionId =
            attributeValues.values().stream()
                .collect(Collectors.toMap(
                    pav -> pav.attribute_definition().value(),
                    Function.identity()
                ));

        for (CategoryAttribute categoryAttr : attrs) {

            String defId = categoryAttr.attribute_definition().id().value();
            ProductAttributeValue productAttr = productByDefinitionId.get(defId);

            if (categoryAttr.is_required() && productAttr == null) {
                throw new IllegalStateException(
                    "The product attribute is missing for: " + defId
                );
            }

            if (productAttr != null) {
                productAttr.validTypes(categoryAttr.attribute_definition());
            }
        }
    }

    public void addProductAttribute(Me me, ProductAttributeValue attr) {
        if(me == null)
            throw new RuntimeException("You must be authenticated to hacer this action");

        me.IHavePermission(Permission.updateProduct());

        attributeValues.values().stream().forEach(a -> {
            if(a.attribute_definition().equals(attr.attribute_definition()))
                throw new RuntimeException("An attribute with the same attribute definition already exists.");
        });

        attributeValues.put(attr.id().value(), attr);
    }

    public void update(
        Me me,
        String title, 
        Slug slug, 
        String description,
        List<String> categories,
        Price price,
        Quantity stock,
        List<String> images,
        List<ProductAttributeValue> attributes,
        List<String> tags
    ) { 
        if(me == null)
            throw new RuntimeException("You must be authenticated to hacer this action");

        me.IHavePermission(Permission.updateProduct());
        
        List<ProductAttributeValue> newAttrs = attributes;

        Map<String, ProductAttributeValue> mapNewAttrs = new HashMap<>();

        newAttrs.stream().forEach(a -> {
            mapNewAttrs.put(a.id().value(), a);
        });

        attributeValues.values().stream().forEach(a -> {
            ProductAttributeValue attr = mapNewAttrs.get(a.id().value());

            if(attr == null)
                throw new RuntimeException("you need to thicken the attribute " + a.id().value());
        });

        this.title = title;
        this.description = description;
        this.slug = slug;
        this.categories = categories;
        this.price = price;
        this.stock = stock;
        this.images = images;
        this.tags = tags;
    }

    public void removeAttribute(Me me, Id productAttributeId) {
        if(me == null)
            throw new RuntimeException("You must be authenticated to hacer this action");

        me.IHavePermission(Permission.updateProduct());

        if(attributeValues.get(productAttributeId.value()) == null)
            throw new DataNotFound("Product attribute not found");

        attributeValues.remove(productAttributeId.value());
    }

    public void delete(Me me) {
        if(me == null)
            throw new RuntimeException("You must be authenticated to hacer this action");

        me.IHavePermission(Permission.deleteProduct());
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
        return List.copyOf(categories);
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

    public List<String> tags() {
        return tags;
    }
}
