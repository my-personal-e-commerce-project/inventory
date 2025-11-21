package microservice.cloud.inventory.category.application.ports.in;

import microservice.cloud.inventory.category.domain.entity.Category;

public interface UpdateCategoryUseCasePort {

    public void execute(Category category);
}
