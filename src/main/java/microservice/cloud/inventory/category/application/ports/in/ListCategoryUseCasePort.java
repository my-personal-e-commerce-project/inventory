package microservice.cloud.inventory.category.application.ports.in;

import microservice.cloud.inventory.category.domain.entity.Category;
import microservice.cloud.inventory.shared.application.dto.Pagination;

public interface ListCategoryUseCasePort {

    public Pagination<Category> execute(int page, int limit);
}
