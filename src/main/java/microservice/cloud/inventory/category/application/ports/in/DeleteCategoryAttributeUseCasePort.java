package microservice.cloud.inventory.category.application.ports.in;

import microservice.cloud.inventory.shared.domain.value_objects.Id;

public interface DeleteCategoryAttributeUseCasePort {

    public void execute(Id categoryId, Id categoryAttributeId);
}
