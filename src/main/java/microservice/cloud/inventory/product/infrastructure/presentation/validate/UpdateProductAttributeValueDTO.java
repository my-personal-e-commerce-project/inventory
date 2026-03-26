package microservice.cloud.inventory.product.infrastructure.presentation.validate;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record UpdateProductAttributeValueDTO (

    @NotNull
    @NotEmpty
    String id,
   
    @NotNull
    @NotEmpty
    String attribute_definition_id,

    String string_value,
    Integer integer_value,
    Double double_value,
    Boolean boolean_value
) {}
