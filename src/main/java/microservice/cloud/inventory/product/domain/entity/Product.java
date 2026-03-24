package microservice.cloud.inventory.product.domain.entity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import microservice.cloud.inventory.attribute.domain.entity.AttributeDefinition;
import microservice.cloud.inventory.category.domain.entity.CategoryAttribute;
import microservice.cloud.inventory.product.domain.exception.InvalidProductException;
import microservice.cloud.inventory.product.domain.value_objects.Price;
import microservice.cloud.inventory.product.domain.value_objects.Quantity;
import microservice.cloud.inventory.shared.domain.exception.DataNotFound;
import microservice.cloud.inventory.shared.domain.value_objects.Id;
import microservice.cloud.inventory.shared.domain.value_objects.Me;
import microservice.cloud.inventory.shared.domain.value_objects.Permission;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;

public class Product {
    private Id id;
    private String title;
    private Slug slug;
    private String description;
    private Set<String> categories;
    private Price price;
    private Map<String, ProductAttributeValue> attributeValues = new HashMap<>();
    private Quantity stock;
    private Set<String> images;
    private Set<String> tags;

    public Product(
        Id id,
        String title,
        Slug slug,
        String description,
        Set<String> categories, 
        Price price, 
        Set<ProductAttributeValue> attributeValues,
        Quantity stock,
        Set<String> images,
        Set<String> tags
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

    public static Product factory(
        Me me,
        String title,
        Slug slug,
        String description,
        Set<String> categories, 
        Price price, 
        Set<ProductAttributeValue> attributeValues,
        Quantity stock,
        Set<String> images,
        Set<String> tags
    ) {
        if(me == null)
            throw new RuntimeException("You do not have permission to perform this action");

        me.IHavePermission(Permission.createProduct());

        return new Product(
            Id.generate(), 
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
    }

    public void validGlobalAttributeDefinitions(List<AttributeDefinition> attrs) {
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

    public void validCategoryAttributes(Set<CategoryAttribute> category_attrs) {
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
                    + categoryAttr.attribute_definition().id().value() 
                    + ", go create a new product attribute"
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
        Set<String> categories,
        Price price,
        Set<ProductAttributeValue> attributes,
        Quantity stock,
        Set<String> images,
        Set<String> tags
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
            ProductAttributeValue attr = this.attributeValues.get(a.id().value());

            if(attr == null)
                throw new RuntimeException(
                    "Product attribute with id: %s not found"
                    .formatted(a.id().value())
                );

            if(!attr.attribute_definition_id().equals(a.attribute_definition_id()))
                throw new RuntimeException(
                    "The id of the attribute definition of product attribute value: " 
                    + a.id().value() 
                    + ", should be " 
                    + a.attribute_definition_id().value()
                );
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

        if(categoryAttribute.is_required())
            throw 
                new RuntimeException("This product attribute is required by one of your categories; this product attribute cannot be removed.");

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

    public Set<String> categories() {
        return new HashSet<>(categories);
    }

    public Price price() {
        return price;
    }

    public Set<ProductAttributeValue> attributeValues() {
        return new HashSet<>(attributeValues.values());
    }

    public Quantity stock() {
        return stock;
    }

    public Set<String> images() {
        return new HashSet<>(images);
    }

    public Set<String> tags() {
        return new HashSet<>(tags);
    }
}
