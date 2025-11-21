package microservice.cloud.inventory.category.infrastructure.entity;

import java.util.List;
import java.util.ArrayList;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Column(name = "name", unique = true, nullable = false, updatable = false)
    private String name;

    @Column(name = "slug", unique = true, nullable = false, updatable = false)
    private String slug;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "parent_id", nullable = false, updatable = false)
    private CategoryEntity parent;

    @Builder.Default
    @OneToMany(
        mappedBy = "category",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<CategoryAttributeEntity> categoryAttributes = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CategoryEntity> children = new ArrayList<>();
}
