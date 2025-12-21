package microservice.cloud.inventory.product.infrastructure.dtos.event;

public record ProductAttributeValueEvent (
    String attribute_definition,
    String string_value,
    Integer integer_value,
    Double double_value,
    Boolean boolean_value
) {}
