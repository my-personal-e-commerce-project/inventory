package microservice.cloud.inventory.category.infrastructure.presentation.validate;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record UpdateCategoryAttributeDTO (
    @NotNull
    @NotEmpty
    String id,
    @Valid
    @NotNull
    String attribute_definition_id,
    @NotNull
    Boolean is_required,
    @NotNull
    Boolean is_filterable,
    @NotNull
    Boolean is_sortable
) {}
