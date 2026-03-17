package microservice.cloud.inventory.product.application.dtos;

public record ProductAttributeValueReadDTO (
    String id,
    String attribute_definition_slug,
    String attribute_definition_id,
    String string_value,
    Integer integer_value,
    Double double_value,
    Boolean boolean_value
) {}
