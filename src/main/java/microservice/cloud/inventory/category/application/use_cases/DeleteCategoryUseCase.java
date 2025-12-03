package microservice.cloud.inventory.category.application.use_cases;

import microservice.cloud.inventory.category.application.ports.in.DeleteCategoryUseCasePort;
import microservice.cloud.inventory.category.domain.repository.CategoryRepository;
import microservice.cloud.inventory.shared.domain.value_objects.Id;

public class DeleteCategoryUseCase implements DeleteCategoryUseCasePort {

    private final CategoryRepository categoryRepository;

    public DeleteCategoryUseCase(
        CategoryRepository categoryRepository
    ) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void execute(Id category) {
        categoryRepository.delete(category);
    }
}
