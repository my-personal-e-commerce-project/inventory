package microservice.cloud.inventory.category.domain.repository;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import microservice.cloud.inventory.category.domain.entity.Category;
import microservice.cloud.inventory.category.domain.entity.CategoryAttribute;
import microservice.cloud.inventory.shared.domain.value_objects.Id;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;

public interface CategoryRepository {

    void save(Category category);
    Category updateIfExists(Id id, Consumer<Category> function);
    void delete(Category category);

    Category findBySlug(Slug slug);
    Category findById(Id id);
    List<CategoryAttribute> getCategoryAttributesWithAttributeDefinitionsByCategoryIds(Set<String> ids);
    CategoryAttribute getCategoryAttributeByAttributeDefinitionId(Id id);
    CategoryAttribute getCategoryAttributeById(Id id);
}
