package microservice.cloud.inventory.product.domain.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import microservice.cloud.inventory.attribute.domain.entity.AttributeDefinition;
import microservice.cloud.inventory.category.domain.entity.CategoryAttribute;
import microservice.cloud.inventory.coupon.domain.entity.Coupon;
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
    private Set<String> coupons;
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
        Set<String> coupons,
        Quantity stock,
        Set<String> images,
        Set<String> tags
    ) {
        if(title == null)
            throw new InvalidProductException("The title cannot be null");

        this.id = id;
        this.title = title;
        this.slug = slug;
        this.description = description;
        this.categories = categories;
        attributeValues.stream().forEach(attr -> this.attributeValues.put(attr.id().value(), attr));
        this.coupons = coupons;
        this.price = price;
        this.stock = stock;
        this.images = images;
        this.tags = tags;
    }

    public static Product factory(
        Me me,
        Id id,
        String title,
        Slug slug,
        String description,
        Set<String> categories, 
        Price price, 
        Set<ProductAttributeValue> attributeValues,
        Set<Coupon> coupons,
        Quantity stock,
        Set<String> images,
        Set<String> tags
    ) {
        if(me == null)
            throw new RuntimeException("You do not have permission to perform this action");

        me.IHavePermission(Permission.createProduct());

        Product product = new Product(
            id, 
            title, 
            slug, 
            description, 
            categories, 
            price, 
            attributeValues,
            null,
            stock, 
            images, 
            tags
        );

        coupons
            .stream()
            .forEach(c -> {
                product.applyCoupon(c);
            });

        return product;
    }

    public void applyCoupon(Coupon coupon) {
        if(coupon.autoApply())
            throw new RuntimeException("This coupon has already been applied by default.");
      
        if(coupons.contains(coupon.id().value())) {
            throw new RuntimeException("This coupon has already been applied.");
        }

        if(coupon.validAllCategories() && !categories.containsAll(coupon.allowedCategories()))
            throw new RuntimeException("This coupon cannot be applied to this product, this product does not have all specified categories.");

        if(Collections.disjoint(categories, coupon.allowedCategories()))
            throw new RuntimeException("This coupon cannot be applied to this product, this product does not have nor a specified category.");

        if(!price.isGreater(coupon.minPrice()))
            throw new RuntimeException();

        if(price.isLessThan(coupon.minPrice()))
            throw new RuntimeException();

        coupons.add(coupon.id().value());
    }

    public void removeCoupon(Coupon coupon) {
        if(!coupons.contains(coupon.id().value()))
            throw new RuntimeException("Coupon not found in this product.");
        
        coupons.remove(coupon.id().value());
    }

    public void validGlobalAttributesAndCategoryAttributes(Set<AttributeDefinition> globalAttrs, Set<CategoryAttribute> catAttrs) {
        Map<String, ProductAttributeValue> productAttributeByAttributeDefinitionId =
            attributeValues.values().stream()
                .collect(Collectors.toMap(
                    pav -> pav.attribute_definition_id().value(),
                    Function.identity()
                ));

        List<String> validatedIds = new ArrayList<>();

        for (AttributeDefinition attr : globalAttrs) {
            String def = attr.id().value();
            ProductAttributeValue productAttr = 
                productAttributeByAttributeDefinitionId
                .get(def);

            if (productAttr == null) {
                throw new IllegalStateException(
                    "The product attribute is missing for: " 
                    + attr.slug().value() 
                    + ", it is global attribute definition. Go create a new attribute in the appropriate endpoint, the id is: " 
                    + attr.id().value()
                );
            }

            if (productAttr != null) {
                productAttr.validTypes(attr);
            }

            validatedIds.add(def);
        }
  
        for (CategoryAttribute categoryAttr : catAttrs) {

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
            
            validatedIds.add(def);
        }
        
        Set<String> result = productAttributeByAttributeDefinitionId.keySet();
        result.removeAll(validatedIds);

        if(result.size() != 0)
            throw new RuntimeException("The next ids: %s, do not defined by the categories or attribute definition".formatted(validatedIds));
    }

    public void addProductAttribute(Me me, ProductAttributeValue attr) {
        if(me == null)
            throw new RuntimeException("You do not have permission to perform this action");

        me.IHavePermission(Permission.updateProduct());

        attributeValues.values().stream().forEach(a -> {
            if(a.attribute_definition_id().equals(attr.attribute_definition_id()))
                throw new RuntimeException("An product attribute with the same 'attribute definition id' already exists.");
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
        Set<Coupon> coupons,
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
            ProductAttributeValue attr = this.attributeValues.get(a.id().value());

            if(attr == null)
                throw new RuntimeException(
                    "'Product attribute value' with id: %s not found"
                    .formatted(a.id().value())
                );
            
            mapNewAttrs.put(a.id().value(), a);
        });

        coupons.forEach(c -> this.applyCoupon(c));

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
            throw new DataNotFound("The 'product attribute value' " + productAttributeId.value() + " is not of this product");

        if(categoryAttribute != null && categoryAttribute.is_required())
            throw 
                new RuntimeException("This 'product attribute value' is required by one of its categories; this 'product attribute value' cannot be removed.");

        attributeValues.remove(productAttributeId.value());
    }

    public static void canIDeleteThisProduct(Me me) {
        if(me == null)
            throw new RuntimeException("You must be authenticated to do this action");

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

    public List<ProductAttributeValue> attributeValues() {
        return new ArrayList<>(attributeValues.values());
    }

    public List<String> coupons() {
        return new ArrayList<>(coupons);
    }

    public Quantity stock() {
        return stock;
    }

    public List<String> images() {
        return images == null? null: new ArrayList<>(images);
    }

    public Set<String> tags() {
        return tags == null? null: new HashSet<>(tags);
    }
}
