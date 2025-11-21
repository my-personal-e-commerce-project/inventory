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
import microservice.cloud.inventory.attribute.infrastructure.entity.AttributeDefinitionEntity;

@Entity
@Table(name = "category_attributes")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryAttributeEntity {

    @Id
    @Column(name = "id", unique = true, nullable = false, updatable = false)
    private String id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryEntity category;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "attribute_definition_id", nullable = false)
    private AttributeDefinitionEntity attribute_definition;

    @Column(name = "is_required", nullable = false)
    private Boolean is_required;

    @Column(name = "is_filterable", nullable = false)
    private Boolean is_filterable;

    @Column(name = "is_sortable", nullable = false)
    private Boolean is_sortable;
}
