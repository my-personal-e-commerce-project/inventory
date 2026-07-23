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
    Set<String> categories,
    @NotNull
    Boolean isActive,
    @Valid
    @NotNull
    Set<UpdateProductAttributeValueDTO> attributes,
    Set<String> discounts,
    @NotNull
    Double price,
    Integer minStock,
    Set<String> tags
) {}
