package microservice.cloud.inventory.category.application.use_cases;

import microservice.cloud.inventory.category.domain.entity.Category;
import microservice.cloud.inventory.category.domain.repository.CategoryRepository;
import microservice.cloud.inventory.shared.domain.value_objects.Id;

public class RealDeleteCategoryUseCase {

    private CategoryRepository categoryRepository;

    public RealDeleteCategoryUseCase(
        CategoryRepository categoryRepository
    ) {
        this.categoryRepository = categoryRepository;
    }
    
    public void execute(Id id) {
        Category category = categoryRepository.findById(id);

        category.deleteCategory();

        categoryRepository.delete(category);
    }
}
