package microservice.cloud.inventory.category.application.use_cases;

import java.util.List;
import java.util.Set;

import microservice.cloud.inventory.category.application.dtos.CategoryReadDTO;
import microservice.cloud.inventory.category.application.ports.out.CategoryReadRepository;

public class ListCategoriesByIdsUseCase {

    private final CategoryReadRepository categoryReadRepository;

    public ListCategoriesByIdsUseCase(
        CategoryReadRepository categoryReadRepository
    ) {

        this.categoryReadRepository = categoryReadRepository;
    }

    public List<CategoryReadDTO> execute(Set<String> ids) {

        return categoryReadRepository.getCategoriesByIds(ids);
    }
}
