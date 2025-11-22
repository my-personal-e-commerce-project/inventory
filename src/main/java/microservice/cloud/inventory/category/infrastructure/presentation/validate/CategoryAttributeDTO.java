package microservice.cloud.inventory.category.infrastructure.presentation.validate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryAttributeDTO {

    @Builder.Default
    public String id = null;
    @Builder.Default
    public String attribute_definition_id = null;
    @NotNull
    @NotBlank
    public String attribute_definition_name;
    @NotNull
    @NotBlank
    public String attribute_definition_slug;
    @NotNull
    @NotBlank
    @Pattern(regexp = "^(STRING|INTEGER|DOUBLE|BOOLEAN|ENUMERATION)$", 
        message = "Status must be STRING, INTEGER, DOUBLE, BOOLEAN or ENUMERATION")
    public String attribute_definition_type;
    @NotNull
    @NotBlank
    public boolean attribute_definition_is_global;
    @NotNull
    @NotBlank
    public Boolean is_required;
    @NotNull
    @NotBlank
    public Boolean is_filterable;
    @NotNull
    @NotBlank
    public Boolean is_sortable;
}
