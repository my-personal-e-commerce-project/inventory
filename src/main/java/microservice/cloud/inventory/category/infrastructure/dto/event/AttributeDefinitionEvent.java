package microservice.cloud.inventory.category.infrastructure.dto.event;

public record AttributeDefinitionEvent (
     String id,
     String name,
     String slug,
     String type
) {}
