package microservice.cloud.inventory.category.domain.repository;

import java.util.List;

import microservice.cloud.inventory.category.domain.entity.Category;
import microservice.cloud.inventory.category.domain.entity.CategoryAttribute;
import microservice.cloud.inventory.shared.domain.value_objects.Id;

public interface CategoryRepository {

    public void save(Category category);
    public void update(Category category);
    public void delete(Category category);
    public Category findById(Id id);
    public List<CategoryAttribute> getCategoryAttributesByCategoryIds(List<String> ids);
}
