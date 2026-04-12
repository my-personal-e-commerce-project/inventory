package microservice.cloud.inventory.product.infrastructure.presentation.validate;

import java.util.Set;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record UpdateProductDTO (
    @NotNull
    @NotEmpty
    String title,
    @NotNull
    @NotEmpty
    String slug,
    @NotNull
    @NotEmpty
    String description,
    @NotNull
    @NotEmpty
    Set<String> categories,
    @Valid
    @NotNull
    Set<UpdateProductAttributeValueDTO> attributes,
    Set<String> discounts,
    @NotNull
    Double price,
    @NotNull
    int stock,
    Set<String> tags
) {}
