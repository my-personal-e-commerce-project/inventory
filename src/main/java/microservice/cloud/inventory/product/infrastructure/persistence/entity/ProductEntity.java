package microservice.cloud.inventory.product.infrastructure.persistence.entity;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Table("products")
@Getter
@AllArgsConstructor
public class ProductEntity {

    @Table("product_categories")
    public static record ProductCategoryReference(
        @Column("category_id") String categoryId
    ) {}

    @Table("discount_categories")
    public static record ProductDiscountReference(
        @Column("discount_id") String discountId
    ) {}

    @Id
    private String id;
    
    private String title;
    
    private String slug;

    private String description;

    @MappedCollection(idColumn = "product_id")
    private Set<ProductCategoryReference> categories = new HashSet<>();

    @MappedCollection(idColumn = "product_id")
    private Set<ProductDiscountReference> discounts = new HashSet<>();

    private Double price;
    
    private int stock;

    private Set<String> images;

    @MappedCollection(idColumn = "product_id")
    private Set<ProductAttributeValueEntity> attributeValues;

    private Set<String> tags;
}
