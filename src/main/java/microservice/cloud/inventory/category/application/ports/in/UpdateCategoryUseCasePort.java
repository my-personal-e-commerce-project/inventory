package microservice.cloud.inventory.category.application.ports.in;

import java.util.List;

import microservice.cloud.inventory.category.domain.entity.CategoryAttribute;
import microservice.cloud.inventory.shared.domain.value_objects.Id;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;

public interface UpdateCategoryUseCasePort {

    public void execute(Id id, String name, Slug slug, Id parent_id, List<CategoryAttribute> categoryAttributes);
}
