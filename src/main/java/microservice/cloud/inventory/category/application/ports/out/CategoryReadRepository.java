package microservice.cloud.inventory.category.application.ports.out;

import microservice.cloud.inventory.category.domain.entity.Category;
import microservice.cloud.inventory.shared.application.dto.Pagination;
import microservice.cloud.inventory.shared.domain.value_objects.Id;

public interface CategoryReadRepository {

    public Pagination<Category> findAll(int page, int limit);
    public Category findById(Id id);
}
