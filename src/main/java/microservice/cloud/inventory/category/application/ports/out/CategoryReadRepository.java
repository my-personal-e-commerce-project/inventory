package microservice.cloud.inventory.category.application.ports.out;

import microservice.cloud.inventory.category.application.dtos.CategoryReadDTO;
import microservice.cloud.inventory.shared.application.dto.Pagination;

public interface CategoryReadRepository {

    public Pagination<CategoryReadDTO> findAll(int page, int limit);
}
