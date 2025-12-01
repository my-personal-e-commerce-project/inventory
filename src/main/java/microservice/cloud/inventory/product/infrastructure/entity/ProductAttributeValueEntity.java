package microservice.cloud.inventory.product.infrastructure.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import microservice.cloud.inventory.attribute.infrastructure.entity.AttributeDefinitionEntity;

@Entity
@Table(name = "product_attribute_values")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductAttributeValueEntity {

    @Id
    @Column(nullable = false, unique = false, updatable = false)
    private String id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private ProductEntity product;
   
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(nullable = false)
    private AttributeDefinitionEntity attribute_definition;

    private String string_value;
    private Integer integer_value; 
    private Double double_value;
    private Boolean boolean_value;
}
