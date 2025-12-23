package microservice.cloud.inventory.product.infrastructure.presentation.validate;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class ProductAttributeValueDTO {

    @Builder.Default
    private String id = null;

    @NotNull
    @NotEmpty
    private String attribute_definition_slug;

    @Builder.Default
    private String string_value=null;
    
    @Builder.Default
    private Integer integer_value=null;
    
    @Builder.Default
    private Double double_value=null;
    
    @Builder.Default
    private Boolean boolean_value=null;
}
