package microservice.cloud.inventory.category.application.ports.out;

import java.util.List;
import java.util.Set;

import microservice.cloud.inventory.category.application.dtos.CategoryReadDTO;
import microservice.cloud.inventory.category.application.dtos.QueryCategories;
import microservice.cloud.inventory.shared.application.dto.Pagination;

public interface CategoryReadRepository {

    public List<CategoryReadDTO> getCategoriesByIds(Set<String> ids);
    public Pagination<CategoryReadDTO> findAll(QueryCategories query, int page, int limit);
}
