package microservice.cloud.inventory.category.application.ports.out;

import microservice.cloud.inventory.category.application.dtos.CategoryReadDTO;
import microservice.cloud.inventory.shared.application.dto.Pagination;
import microservice.cloud.inventory.shared.domain.value_objects.Id;

public interface CategoryReadRepository {

    public Pagination<CategoryReadDTO> findAll(int page, int limit);
    public CategoryReadDTO findById(Id id);
}
