package microservice.cloud.inventory.category.infrastructure.presentation.validate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class AttributeDefinitionDTO {

    @Builder.Default
    public String id = null;

    @NotNull
    @NotBlank
    public String name;
    
    @NotNull
    @NotBlank
    public String slug;

    @NotNull
    @NotBlank
    @Pattern(regexp = "^(STRING|INTEGER|DOUBLE|BOOLEAN|ENUMERATION)$", 
        message = "Status must be STRING, INTEGER, DOUBLE, BOOLEAN")
    public String type;

    @Builder.Default
    public Boolean is_global = null;
}
