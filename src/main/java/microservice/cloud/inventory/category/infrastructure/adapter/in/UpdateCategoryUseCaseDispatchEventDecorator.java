package microservice.cloud.inventory.category.infrastructure.adapter.in;

import java.util.List;

import lombok.RequiredArgsConstructor;
import microservice.cloud.inventory.category.application.ports.in.UpdateCategoryUseCasePort;
import microservice.cloud.inventory.category.domain.entity.Category;
import microservice.cloud.inventory.category.domain.entity.CategoryAttribute;
import microservice.cloud.inventory.category.infrastructure.dto.event.CategoryUpdatedEvent;
import microservice.cloud.inventory.shared.application.ports.out.EventPublishedPort;
import microservice.cloud.inventory.shared.domain.value_objects.Id;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;

@RequiredArgsConstructor
public class UpdateCategoryUseCaseDispatchEventDecorator implements UpdateCategoryUseCasePort {

    private final EventPublishedPort eventPublishedPort;
    private final UpdateCategoryUseCasePort updateCategoryUseCasePort;
    
    @Override
    public Category execute(Id id, String name, Slug slug, Id parent_id, List<CategoryAttribute> categoryAttributes) {
        Category category = updateCategoryUseCasePort.execute(id, name, slug, parent_id, categoryAttributes);

        eventPublishedPort.publish(List.of(
            new CategoryUpdatedEvent(
                category.id().value(), 
                category.name(), 
                category.slug().value(), 
                category.parent_id() == null
                    ? null
                    : category.parent_id().value(), 
                category.categoryAttributes()
            )
        ));

        return category;
    }
}
