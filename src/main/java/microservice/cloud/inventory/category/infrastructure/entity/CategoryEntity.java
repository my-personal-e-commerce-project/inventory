package microservice.cloud.inventory.category.infrastructure.entity;

import java.util.List;
import java.util.ArrayList;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PreRemove;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import microservice.cloud.inventory.product.infrastructure.entity.ProductEntity;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "categories")
public class CategoryEntity {

    @Id
    @Column(name = "id", unique = true, nullable = false, updatable = false)
    private String id; 

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @Column(name = "slug", unique = true, nullable = false)
    private String slug;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private CategoryEntity parent;

    @Builder.Default
    @OneToMany(
        mappedBy = "category",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<CategoryAttributeEntity> categoryAttributes = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CategoryEntity> children = new ArrayList<>();

    @ManyToMany(mappedBy = "categories")
    private List<ProductEntity> products = new ArrayList<>();

    @PreRemove
    private void preRemove() {
        for (ProductEntity product : products) {
            product.getCategories().remove(this);
        }
    }
}
