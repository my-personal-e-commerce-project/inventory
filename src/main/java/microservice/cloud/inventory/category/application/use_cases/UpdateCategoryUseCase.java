package microservice.cloud.inventory.category.application.use_cases;

import microservice.cloud.inventory.category.application.ports.in.UpdateCategoryUseCasePort;
import microservice.cloud.inventory.category.domain.entity.Category;
import microservice.cloud.inventory.category.domain.repository.CategoryRepository;

public class UpdateCategoryUseCase implements UpdateCategoryUseCasePort {
    private final CategoryRepository categoryRepository;

    public UpdateCategoryUseCase(
        CategoryRepository categoryRepository
    ) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void execute(Category category) {
        Category categoryDB = categoryRepository.findById(category.id());

        categoryDB.update(category);

        categoryRepository.update(categoryDB);
    }
}
