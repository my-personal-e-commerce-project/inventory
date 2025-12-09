package microservice.cloud.inventory.category.domain.event;

public record CategoryAttributeEvent(
    String name,
    String slug,
    String type,
    Boolean is_required,
    Boolean is_filterable,
    Boolean is_sortable
) {}
