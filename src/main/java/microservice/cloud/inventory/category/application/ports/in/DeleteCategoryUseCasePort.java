package microservice.cloud.inventory.category.application.ports.in;

import microservice.cloud.inventory.shared.domain.value_objects.Slug;

public interface DeleteCategoryUseCasePort {

    public void execute(Slug find_slug);
}
