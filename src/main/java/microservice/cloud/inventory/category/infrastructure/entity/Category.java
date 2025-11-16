package microservice.cloud.inventory.category.infrastructure.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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
public class Category {

    @Id
    @Column(name = "id", unique = true, nullable = false, updatable = false)
    private String id; 

    @Column(name = "name", unique = true, nullable = false, updatable = false)
    private String name;

    @Column(name = "slug", unique = true, nullable = false, updatable = false)
    private String slug;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "parent_id", unique = true, nullable = false, updatable = false)
    private Category parent;
}
