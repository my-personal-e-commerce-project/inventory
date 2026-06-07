package microservice.cloud.inventory.category.application.use_cases;

import microservice.cloud.inventory.category.domain.entity.Category;
import microservice.cloud.inventory.category.domain.repository.CategoryRepository;
import microservice.cloud.inventory.shared.domain.value_objects.Id;

public class EnabledCategoryUseCase {
   
    private CategoryRepository categoryRepository;

    public EnabledCategoryUseCase(
        CategoryRepository categoryRepository
    ) {
        this.categoryRepository = categoryRepository;
    }
    
    public void execute(Id id) {
        Category category = categoryRepository.findById(id);

        category.enabledCategory();

        try {
            categoryRepository.update(category);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
