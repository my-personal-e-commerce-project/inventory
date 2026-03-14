package microservice.cloud.inventory.attribute.infrastructure.presentation.validate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record AttributeDefinitionDTO(
    String id,
    @NotBlank(message = "Name is required")
    String name,
    @NotBlank(message = "Slug is required")
    String slug,
    @NotBlank
    @Pattern(
        regexp = "^(STRING|INTEGER|DOUBLE|BOOLEAN|ENUMERATION)$", 
        message = "Type must be STRING, INTEGER, DOUBLE, BOOLEAN or ENUMERATION"
    )
    String type,
    @NotNull
    Boolean is_global
) {
    public AttributeDefinitionDTO(String name, String slug, String type, Boolean isGlobal) {
        this(null, name, slug, type, isGlobal);
    }
}
