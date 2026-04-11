package microservice.cloud.inventory.category.domain.repository;

import java.util.List;
import java.util.Set;

import microservice.cloud.inventory.category.domain.entity.Category;
import microservice.cloud.inventory.category.domain.entity.CategoryAttribute;
import microservice.cloud.inventory.shared.domain.value_objects.Id;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;

public interface CategoryRepository {

    void save(Category category);
    void update(Category category);
    void delete(Category category);
    Category findBySlug(Slug slug);
    List<CategoryAttribute> getCategoryAttributesWithAttributeDefinitionsByCategoryIds(Set<String> ids);
    CategoryAttribute getCategoryAttributeByAttributeDefinitionId(Id id);
    CategoryAttribute getCategoryAttributeById(Id id);
}
