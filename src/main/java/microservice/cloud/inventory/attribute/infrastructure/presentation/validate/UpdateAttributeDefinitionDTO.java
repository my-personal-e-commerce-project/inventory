package microservice.cloud.inventory.attribute.infrastructure.presentation.validate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record UpdateAttributeDefinitionDTO (
    String id,
    @NotNull
    @NotBlank
    String name,
    @NotNull
    @NotBlank
    String slug,
    @NotNull
    @NotBlank
    @Pattern(regexp = "^(STRING|INTEGER|DOUBLE|BOOLEAN|ENUMERATION)$", 
        message = "Status must be STRING, INTEGER, DOUBLE, BOOLEAN")
    String type,
    Boolean is_global
) {}
