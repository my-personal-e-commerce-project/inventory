package microservice.cloud.inventory.product.infrastructure.persistence.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table("product_attribute_values")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductAttributeValueEntity {

    @Id
    private String id;
    
    private String product_id;
  
    private String attribute_definition_id;

    private String string_value;
    
    private Integer integer_value; 
    
    private Double double_value;
    
    private Boolean boolean_value;
}
