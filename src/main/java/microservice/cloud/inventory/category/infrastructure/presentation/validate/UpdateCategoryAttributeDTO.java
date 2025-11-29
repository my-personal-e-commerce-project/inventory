package microservice.cloud.inventory.category.infrastructure.presentation.validate;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
public class UpdateCategoryAttributeDTO {

    @NotNull
    @NotEmpty
    private String id;
  
    @Valid
    @NotNull
    @NotEmpty
    private UpdateAttributeDefinitionDTO attributeDefinition;

    @NotNull
    private Boolean is_required;

    @NotNull
    private Boolean is_filterable;

    @NotNull
    private Boolean is_sortable;
}
