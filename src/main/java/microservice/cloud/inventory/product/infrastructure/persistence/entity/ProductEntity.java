package microservice.cloud.inventory.product.infrastructure.persistence.entity;

import java.util.HashSet;
import java.util.List;
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

    @Id
    private String id;
    
    private String title;
    
    private String slug;

    private String description;

    @MappedCollection(idColumn = "product_id")
    private Set<ProductCategoryReference> categories = new HashSet<>();

    private Double price;
    
    private int stock;

    private List<String> images;

    @MappedCollection(idColumn = "product_id")
    private Set<ProductAttributeValueEntity> attributeValues;

    private List<String> tags;
}
