package microservice.cloud.inventory.product.infrastructure.persistence.entity;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import microservice.cloud.inventory.product.domain.entity.Product;

@Table("products")
@Getter
@AllArgsConstructor
public class ProductEntity {

    @Table("product_categories")
    public static record ProductCategoryReference(
        @Column("category_id") String categoryId
    ) {}

    @Id
    private String id;
    
    private String title;
    
    private String slug;

    private String description;

    @MappedCollection(idColumn = "product_id")
    private Set<ProductCategoryReference> categories = new HashSet<>();

    private boolean isActive;

    private Double price;
    
    private Integer minStock;

    private Set<String> images;

    @MappedCollection(idColumn = "product_id")
    private Set<ProductAttributeValueEntity> attributeValues;

    private Set<String> tags;

    @Version
    private Long version;

    public void updateFromDomain(Product product) {
        this.title = product.title();
        this.slug = product.slug().value();
        this.description = product.description();
        this.isActive = product.isActive();
        this.price = product.price().value();
        this.minStock = product.minStock() == null? null: product.minStock().value();
        this.images = product.images();
        this.tags = product.tags();
    }
}
