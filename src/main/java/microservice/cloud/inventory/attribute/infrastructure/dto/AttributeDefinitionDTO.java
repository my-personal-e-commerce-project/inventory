package microservice.cloud.inventory.attribute.infrastructure.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record AttributeDefinitionDTO(
   String id,
   @NotNull
   @NotEmpty
   String name,
   @NotNull
   @NotEmpty
   String slug,
   @NotNull
   @NotEmpty
   @Pattern(regexp = "^(STRING|INTEGER|DOUBLE|BOOLEAN|ENUMERATION)$", 
        message = "Status must be STRING, INTEGER, DOUBLE, BOOLEAN")
   String type
) {

    public AttributeDefinitionDTO(
        String name,
        String slug,
        String type
    ) {
        this(null, name, slug, type);
    }
}
