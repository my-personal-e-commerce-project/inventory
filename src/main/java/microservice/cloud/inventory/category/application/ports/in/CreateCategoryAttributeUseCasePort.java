package microservice.cloud.inventory.category.application.ports.in;

import microservice.cloud.inventory.category.domain.entity.CategoryAttribute;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;

public interface CreateCategoryAttributeUseCasePort {

    public void execute(Slug find_slug, CategoryAttribute categoryAttribute);
}
