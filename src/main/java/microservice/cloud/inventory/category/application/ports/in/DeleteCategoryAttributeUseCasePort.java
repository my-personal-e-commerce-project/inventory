package microservice.cloud.inventory.category.application.ports.in;

import microservice.cloud.inventory.shared.domain.value_objects.Id;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;

public interface DeleteCategoryAttributeUseCasePort {

    public void execute(Slug find_slug, Id categoryAttributeId);
}
