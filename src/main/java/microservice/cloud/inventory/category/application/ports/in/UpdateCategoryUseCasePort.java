package microservice.cloud.inventory.category.application.ports.in;

import java.util.Set;

import microservice.cloud.inventory.category.domain.entity.CategoryAttribute;
import microservice.cloud.inventory.shared.domain.value_objects.Id;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;

public interface UpdateCategoryUseCasePort {

    public void execute(
        Slug find_slug, 
        String name, 
        Slug slug, 
        Id parent_id, 
        Set<CategoryAttribute> categoryAttributes
    );
}
