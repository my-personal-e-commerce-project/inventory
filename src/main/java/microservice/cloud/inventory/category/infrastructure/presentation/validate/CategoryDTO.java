package microservice.cloud.inventory.category.infrastructure.presentation.validate;

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
public class CategoryDTO {

    @Builder.Default
    public String id = null;

    @NotNull
    @NotEmpty
    public String name;

    @NotNull
    @NotEmpty
    public String slug;

    @Builder.Default
    public String parent_id = null;
}
