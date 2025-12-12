package microservice.cloud.inventory.category.application.use_cases;

import microservice.cloud.inventory.category.application.dtos.CategoryReadDTO;
import microservice.cloud.inventory.category.application.ports.in.ListCategoryUseCasePort;
import microservice.cloud.inventory.category.application.ports.out.CategoryReadRepository;
import microservice.cloud.inventory.shared.application.dto.Pagination;

public class ListCategoryUseCase implements ListCategoryUseCasePort {
    
    private final CategoryReadRepository categoryRepository;

    public ListCategoryUseCase(
        CategoryReadRepository categoryRepository
    ) {
        this.categoryRepository = categoryRepository;
    }

    public Pagination<CategoryReadDTO> execute(int page, int limit) {
        return categoryRepository.findAll(page, limit);
    }
}
