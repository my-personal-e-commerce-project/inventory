package microservice.cloud.inventory.category.infrastructure.presentation.validate;

import java.util.Set;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record UpdateCategoryDTO (
    String id,
    @NotNull
    @NotEmpty
    String name,
    @NotNull
    @NotEmpty
    String slug,
    String parent_id,
    @Valid
    @NotNull
    Set<UpdateCategoryAttributeDTO> categoryAttributes
) {}
