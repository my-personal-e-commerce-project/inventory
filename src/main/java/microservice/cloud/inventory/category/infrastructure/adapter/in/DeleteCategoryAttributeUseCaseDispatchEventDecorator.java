package microservice.cloud.inventory.category.infrastructure.adapter.in;

import java.util.List;

import lombok.RequiredArgsConstructor;
import microservice.cloud.inventory.category.application.ports.in.DeleteCategoryAttributeUseCasePort;
import microservice.cloud.inventory.category.domain.entity.Category;
import microservice.cloud.inventory.category.infrastructure.dto.event.CategoryUpdatedEvent;
import microservice.cloud.inventory.shared.application.ports.out.EventPublishedPort;
import microservice.cloud.inventory.shared.domain.value_objects.Id;

@RequiredArgsConstructor
public class DeleteCategoryAttributeUseCaseDispatchEventDecorator implements DeleteCategoryAttributeUseCasePort {

    private final DeleteCategoryAttributeUseCasePort deleteCategoryAttributeUseCasePort;
    private final EventPublishedPort eventPublishedPort;
    
    @Override
    public Category execute(Id categoryId, Id categoryAttributeId) {
        Category category = deleteCategoryAttributeUseCasePort.execute(categoryId, categoryAttributeId);
 
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
