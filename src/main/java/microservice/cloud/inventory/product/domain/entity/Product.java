package microservice.cloud.inventory.product.domain.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import microservice.cloud.inventory.attribute.domain.entity.AttributeDefinition;
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

public class Product extends AggregateRoot {
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

    public void create(
            Me me
    ) {
        if(me == null)
            throw new RuntimeException("You do not have permission to perform this action");

        me.IHavePermission(Permission.createProduct());
    }

    public void validDefaultAttributes(List<AttributeDefinition> attrs) {
        Map<String, ProductAttributeValue> productAttributeByAttributeDefinitionId =
            attributeValues.values().stream()
                .collect(Collectors.toMap(
                    pav -> pav.attribute_definition_id().value(),
                    Function.identity()
                ));

        for (AttributeDefinition attr : attrs) {
            String def = attr.slug().value();
            ProductAttributeValue productAttr = 
                productAttributeByAttributeDefinitionId
                .get(def);

            if (productAttr == null) {
                throw new IllegalStateException(
                    "The product attribute is missing for: " + attr.slug().value() + ", go create a new attribute in the appropriate endpoint"
                );
            }

            if (productAttr != null) {
                productAttr.validTypes(attr);
            }
        }
    }

    public void validAttributes(List<CategoryAttribute> category_attrs) {

        Map<String, ProductAttributeValue> productAttributeByAttributeDefinitionId =
            attributeValues.values().stream()
                .collect(Collectors.toMap(
                    pav -> pav.attribute_definition_id().value(),
                    Function.identity()
                ));

        for (CategoryAttribute categoryAttr : category_attrs) {

            String def = categoryAttr.attribute_definition_id().value();

            ProductAttributeValue productAttr = productAttributeByAttributeDefinitionId.get(def);

            if (categoryAttr.is_required() && productAttr == null) {
                throw new IllegalStateException(
                    "The product attribute is missing for the attribute definition: " 
                    + categoryAttr.attribute_definition().slug().value() + " with id:" 
                    + categoryAttr.attribute_definition().id().value() 
                    + ", go create a new attribute in the appropriate endpoint"
                );
            }

            if (productAttr != null) {
                productAttr.validTypes(categoryAttr.attribute_definition());
            }
        }
    }

    public void addProductAttribute(Me me, ProductAttributeValue attr) {
        if(me == null)
            throw new RuntimeException("You do not have permission to perform this action");

        me.IHavePermission(Permission.updateProduct());

        attributeValues.values().stream().forEach(a -> {
            if(a.attribute_definition_id().equals(attr.attribute_definition_id()))
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
            throw new RuntimeException("You do not have permission to perform this action");

        if(categories == null || categories.size() < 1)
            throw new InvalidProductException("Products must have at least one category");

        me.IHavePermission(Permission.updateProduct());
        
        Map<String, ProductAttributeValue> mapNewAttrs = new HashMap<>();

        attributes.stream().forEach(a -> {
            mapNewAttrs.put(a.id().value(), a);
        });

        attributeValues.values().stream().forEach(a -> {
            ProductAttributeValue attr = mapNewAttrs.get(a.id().value());

            if(attr == null)
                throw new RuntimeException("You need to thicken the attribute " + a.id().value());

            if(!attr.attribute_definition_id().equals(a.attribute_definition_id()))
                throw new RuntimeException("The id of the attribute definition: " + a.attribute_definition_id().value() + ", should be in the attribute: " + a.id().value());

        });

        this.attributeValues = mapNewAttrs;
        this.title = title;
        this.description = description;
        this.slug = slug;
        this.categories = categories;
        this.price = price;
        this.stock = stock;
        this.images = images;
        this.tags = tags;
    }

    public void removeAttribute(Me me, Id productAttributeId, CategoryAttribute categoryAttribute) {
        if(me == null)
            throw new RuntimeException("You do not have permission to perform this action");

        me.IHavePermission(Permission.updateProduct());

        ProductAttributeValue attr = attributeValues.get(productAttributeId.value());

        if(attr == null)
            throw new DataNotFound("The attribute " + productAttributeId.value() + " is not of this product");

        if(categoryAttribute == null)
            throw new RuntimeException("The category attribute must not null");

        if(attr.attribute_definition_id().equals(categoryAttribute.attribute_definition().id()))
            throw 
                new RuntimeException(
                    "The ID of the provided attribute definition is not the same as the ID of the product attribute."
                );

        if(categoryAttribute.is_required())
            throw 
                new RuntimeException("The attribute definition is required, this product attribute cannot be deleted");

        attributeValues.remove(productAttributeId.value());
    }

    public static void delete(Me me) {
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
        return new ArrayList<>(categories);
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
