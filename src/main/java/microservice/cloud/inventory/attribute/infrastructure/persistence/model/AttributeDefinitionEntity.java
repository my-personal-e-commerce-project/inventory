package microservice.cloud.inventory.attribute.infrastructure.persistence.model;

import java.util.List;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import microservice.cloud.inventory.product.infrastructure.entity.ProductAttributeValueEntity;

@Entity
@Table(name = "attribute_definition")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class AttributeDefinitionEntity {

    @Id
    @Column(name = "id", unique = true, nullable = false, updatable = false)
    private String id;
    private String name;
    private String slug;
    private String type;

    @Builder.Default
    private boolean is_global = false;

    @OneToMany(
        mappedBy = "attribute_definition",
        fetch = FetchType.LAZY
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<ProductAttributeValueEntity> productAttributes;
}
