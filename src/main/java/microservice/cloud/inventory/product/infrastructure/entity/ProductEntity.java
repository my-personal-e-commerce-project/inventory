package microservice.cloud.inventory.product.infrastructure.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import microservice.cloud.inventory.category.infrastructure.entity.CategoryEntity;

@Entity
@Table(name = "products")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductEntity {

    @Id
    @Column(nullable = false, unique = true, updatable = false)
    private String id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(nullable = false, unique = true)
    private String slug;

    private String description;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "product_categories",
        joinColumns = @JoinColumn(name = "product_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<CategoryEntity> categories;
   
    @Column(nullable = false)
    private double price;
    
    @Column(nullable = false)
    private int stock;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ImageEntity> images;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ProductAttributeValueEntity> attributeValues;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<TagEntity> tags;


}
