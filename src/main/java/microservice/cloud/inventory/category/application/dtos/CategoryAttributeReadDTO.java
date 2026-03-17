package microservice.cloud.inventory.category.application.dtos;
    
public record CategoryAttributeReadDTO (
    String id,
    AttributeDefinitionReadDTO attributeDefinition,
    Boolean is_required,
    Boolean is_filterable,
    Boolean is_sortable
) {}
