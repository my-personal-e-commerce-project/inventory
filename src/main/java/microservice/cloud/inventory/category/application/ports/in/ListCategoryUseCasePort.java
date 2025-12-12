package microservice.cloud.inventory.category.application.ports.in;

import microservice.cloud.inventory.category.application.dtos.CategoryReadDTO;
import microservice.cloud.inventory.shared.application.dto.Pagination;

public interface ListCategoryUseCasePort {

    public Pagination<CategoryReadDTO> execute(int page, int limit);
}
