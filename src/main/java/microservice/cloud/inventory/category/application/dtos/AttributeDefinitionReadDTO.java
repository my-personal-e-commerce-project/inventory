package microservice.cloud.inventory.category.application.dtos;

public record AttributeDefinitionReadDTO (
    String id,
    String name,
    String slug,
    String type,
    Boolean is_global
) {}
