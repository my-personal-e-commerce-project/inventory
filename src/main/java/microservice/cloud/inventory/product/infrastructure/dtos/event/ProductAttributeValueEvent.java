package microservice.cloud.inventory.product.infrastructure.dtos.event;

public record ProductAttributeValueEvent (
    String attribute_definition_slug,
    String string_value,
    Integer integer_value,
    Double double_value,
    Boolean boolean_value
) {}
