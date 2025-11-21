package microservice.cloud.inventory.category.application.use_cases;

import microservice.cloud.inventory.category.application.ports.in.UpdateCategoryAttributeUseCasePort;
import microservice.cloud.inventory.category.application.ports.out.CategoryReadRepository;
import microservice.cloud.inventory.category.domain.entity.Category;
import microservice.cloud.inventory.category.domain.entity.CategoryAttribute;
import microservice.cloud.inventory.category.domain.repository.CategoryRepository;
import microservice.cloud.inventory.shared.domain.value_objects.Id;

public class UpdateCategoryAttributeUseCase implements UpdateCategoryAttributeUseCasePort {

    private CategoryRepository categoryRepository;
    private CategoryReadRepository categoryReadRepository;

    public UpdateCategoryAttributeUseCase(
        CategoryRepository categoryRepository,
        CategoryReadRepository categoryReadRepository
    ) {
        this.categoryRepository = categoryRepository;
        this.categoryReadRepository = categoryReadRepository;
    }

    @Override
    public void execute(Id category_id, CategoryAttribute categoryAttribute) {
        Category category = categoryReadRepository.findById(category_id);

        category.updateCategoryAttribute(categoryAttribute);

        categoryRepository.syncCategoryAttributes(category);
    }
}
