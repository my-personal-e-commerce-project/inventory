package microservice.cloud.inventory.product.domain.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import microservice.cloud.inventory.attribute.domain.entity.AttributeDefinition;
import microservice.cloud.inventory.category.domain.entity.CategoryAttribute;
import microservice.cloud.inventory.product.domain.event.MinStockAlertEvent;
import microservice.cloud.inventory.product.domain.exception.InvalidProductException;
import microservice.cloud.inventory.product.domain.value_objects.Price;
import microservice.cloud.inventory.product.domain.value_objects.Quantity;
import microservice.cloud.inventory.shared.domain.entity.AggregateRoot;
import microservice.cloud.inventory.shared.domain.exception.DataNotFound;
import microservice.cloud.inventory.shared.domain.value_objects.Id;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;

public class Product extends AggregateRoot {
    private Id id;
    private String title;
    private Slug slug;
    private String description;
    private Set<String> categories;
    private boolean isActive;
    private Price price;
    private Map<String, ProductAttributeValue> attributeValues = new HashMap<>();
    private Quantity minStock;
    private Set<String> images;
    private Set<String> tags;

    public Product(
        Id id,
        String title,
        Slug slug,
        String description,
        Set<String> categories,
        boolean isActive,
        Price price, 
        Set<ProductAttributeValue> attributeValues,
        Quantity minStock,
        Set<String> images,
        Set<String> tags
    ) {
        if(title == null)
            throw new InvalidProductException("The title cannot be null");

        this.minStock = minStock;

        if(minStock == null)
            this.minStock = new Quantity(5);

        
        this.id = id;
        this.title = title;
        this.slug = slug;
        this.description = description;
        this.categories = categories;
        this.isActive = isActive;
        attributeValues.stream().forEach(attr -> this.attributeValues.put(attr.id().value(), attr));
        this.price = price;
        this.images = images;
        this.tags = tags;
    }

    public void update(
        String title, 
        Slug slug, 
        String description,
        Set<String> categories,
        boolean isActive,
        Price price,
        Quantity minStock,
        Set<String> images,
        Set<ProductAttributeValue> attributes,
        Set<String> tags
    ) {
        Map<String, ProductAttributeValue> mapNewAttrs = new HashMap<>();

        attributes.stream().forEach(a -> {
            ProductAttributeValue attr = this.attributeValues.get(a.id().value());

            if(attr == null)
                throw new RuntimeException(
                    "Product attribute value with attribute definition: %s of your new list of attributes, not found in current product attribute values"
                    .formatted(a.attribute_definition_id())
                );
            
            mapNewAttrs.put(a.id().value(), a);
        });

        this.attributeValues().stream().forEach(a -> {
            ProductAttributeValue attr = mapNewAttrs.get(a.id().value());

            if(attr == null)
                throw new RuntimeException(
                    "Product attribute value with id: %s and attribute_definition_id %s not found in your new list of attributes"
                    .formatted(a.id().value(), a.attribute_definition_id().value())
                );
        });

        this.attributeValues = mapNewAttrs;

        this.minStock = minStock;

        if(minStock == null)
            this.minStock = new Quantity(5);

        this.title = title;
        this.slug = slug;
        this.description = description;
        this.categories = categories;
        this.isActive = isActive;
        this.price = price;
        this.images = images;
        this.tags = tags;
    }

    public void minStockReached(Quantity stock) {
        if(stock.isLessThan(this.minStock)) {
            publishEvent(new MinStockAlertEvent(id.value(), stock.value()));
        }

        this.isActive = false;
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
                    + ", this is a global attribute definition. Go create a new attribute, the id is: " 
                    + attr.id().value()
                );
            }

            productAttr.validTypes(attr);
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
                validatedIds.add(def);
            }
            
        }
        
        Set<String> result = productAttributeByAttributeDefinitionId.keySet();
        result.removeAll(validatedIds);

        if(result.size() != 0)
            throw new RuntimeException("The next ids: %s, do not defined by the categories or attribute definition".formatted(result));
    }

    public void addProductAttribute(ProductAttributeValue attr) { 
        attributeValues.values().stream().forEach(a -> {
            if(a.attribute_definition_id().equals(attr.attribute_definition_id()))
                throw new RuntimeException("An product attribute with the same 'attribute definition id' already exists.");
        });

        attributeValues.put(attr.id().value(), attr);
    }

    public void removeProductAttribute(Id productAttributeId, CategoryAttribute categoryAttribute) {
        ProductAttributeValue attr = attributeValues.get(productAttributeId.value());

        if(attr == null)
            throw new DataNotFound("The 'product attribute value' " + productAttributeId.value() + " is not of this product");

        if(categoryAttribute != null && categoryAttribute.is_required())
            throw 
                new RuntimeException("This 'product attribute value' is required by one of its categories; this 'product attribute value' cannot be removed.");

        attributeValues.remove(productAttributeId.value());
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

    public boolean isActive() {
        return isActive;
    }

    public Price price() {
        return price;
    }

    public Set<ProductAttributeValue> attributeValues() {
        return attributeValues == null? null: new HashSet<>(attributeValues.values());
    }

    public Quantity minStock() {
        return minStock;
    }

    public Set<String> images() {
        return images == null? null: new HashSet<>(images);
    }

    public Set<String> tags() {
        return tags == null? null: new HashSet<>(tags);
    }
}
