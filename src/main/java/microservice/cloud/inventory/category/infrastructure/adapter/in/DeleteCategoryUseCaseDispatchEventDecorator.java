package microservice.cloud.inventory.category.infrastructure.adapter.in;

import java.util.List;

import lombok.RequiredArgsConstructor;
import microservice.cloud.inventory.category.application.ports.in.DeleteCategoryUseCasePort;
import microservice.cloud.inventory.category.infrastructure.dto.event.CategoryDeletedEvent;
import microservice.cloud.inventory.shared.application.ports.out.EventPublishedPort;
import microservice.cloud.inventory.shared.domain.value_objects.Id;

@RequiredArgsConstructor
public class DeleteCategoryUseCaseDispatchEventDecorator implements DeleteCategoryUseCasePort {

    private final DeleteCategoryUseCasePort deleteCategoryUseCasePort;
    private final EventPublishedPort eventPublishedPort;

    @Override
    public void execute(Id id) {
       deleteCategoryUseCasePort.execute(id);

       eventPublishedPort.publish(List.of(
            new CategoryDeletedEvent(
                id.value()
            )
        ));
    }
}
