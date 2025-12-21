package microservice.cloud.inventory.category.infrastructure.adapter.in;

import java.util.List;

import lombok.RequiredArgsConstructor;
import microservice.cloud.inventory.category.application.ports.in.CreateCategoryUseCasePort;
import microservice.cloud.inventory.category.domain.entity.Category;
import microservice.cloud.inventory.category.domain.entity.CategoryAttribute;
import microservice.cloud.inventory.category.infrastructure.dto.event.CategoryCreatedEvent;
import microservice.cloud.inventory.shared.application.ports.out.EventPublishedPort;
import microservice.cloud.inventory.shared.domain.value_objects.Id;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;

@RequiredArgsConstructor
public class CreateCategoryUseCaseDispatchEventDecorator implements CreateCategoryUseCasePort {

    private final CreateCategoryUseCasePort createCategoryUseCasePort;
    private final EventPublishedPort eventPublishedPort;
    
    @Override
    public Category execute(Id id, String name, Slug slug, Id parent_id, List<CategoryAttribute> categoryAttributes) {
        Category category = createCategoryUseCasePort.execute(id, name, slug, parent_id, categoryAttributes);
        
        eventPublishedPort.publish(List.of(
            new CategoryCreatedEvent(
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
