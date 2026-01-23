package microservice.cloud.inventory.category.infrastructure.dto.event;

public record CategoryAttributeEvent(
    String id,
    AttributeDefinitionEvent attribute_definition,
    Boolean is_required,
    Boolean is_filterable,
    Boolean is_sortable
) {}
