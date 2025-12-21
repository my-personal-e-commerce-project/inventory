package microservice.cloud.inventory.attribute.application.ports.dto;

public record AttributeDefinitionReadDTO(
   String id,
   String name,
   String slug,
   String type
) {}
