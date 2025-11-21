package microservice.cloud.inventory.category.application.ports.in;

import microservice.cloud.inventory.category.domain.entity.CategoryAttribute;
import microservice.cloud.inventory.shared.domain.value_objects.Id;

public interface AddCategoryAttributeUseCasePort {

    public void execute(Id category, CategoryAttribute categoryAttribute);
}
