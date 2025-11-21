package microservice.cloud.inventory.category.domain.repository;

import microservice.cloud.inventory.category.domain.entity.Category;
import microservice.cloud.inventory.shared.domain.value_objects.Id;

public interface CategoryRepository {

    public void save(Category category);
    public void update(Category category);
    public void syncCategoryAttributes(Category category);
    public void delete(Id id);
}
