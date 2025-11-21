package microservice.cloud.inventory.category.domain.repository;

import microservice.cloud.inventory.category.domain.entity.CategoryAttribute;
import microservice.cloud.inventory.shared.domain.value_objects.Id;

public interface CategoryAttributeRepository {

    public void save(CategoryAttribute categoryAttribute);
    public void update(CategoryAttribute categoryAttribute);
    public void delete(Id id);
}
