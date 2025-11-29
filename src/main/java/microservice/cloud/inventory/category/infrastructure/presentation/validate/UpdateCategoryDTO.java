package microservice.cloud.inventory.category.infrastructure.presentation.validate;

import java.util.List;

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
public class UpdateCategoryDTO {

    @Builder.Default
    private String id = null;

    @NotNull
    @NotEmpty
    private String name;

    @NotNull
    @NotEmpty
    private String slug;

    @Builder.Default
    private String parent_id = null;

    @Valid
    @NotNull
    private List<UpdateCategoryAttributeDTO> categoryAttributes;
}
