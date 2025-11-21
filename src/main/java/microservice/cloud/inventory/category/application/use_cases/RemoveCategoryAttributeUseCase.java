package microservice.cloud.inventory.category.application.use_cases;

import microservice.cloud.inventory.category.application.ports.in.RemoveCategoryAttributeUseCasePort;
import microservice.cloud.inventory.category.application.ports.out.CategoryReadRepository;
import microservice.cloud.inventory.category.domain.entity.Category;
import microservice.cloud.inventory.category.domain.repository.CategoryRepository;
import microservice.cloud.inventory.shared.domain.value_objects.Id;

public class RemoveCategoryAttributeUseCase implements RemoveCategoryAttributeUseCasePort {

    private final CategoryRepository categoryRepository;
    private final CategoryReadRepository categoryReadRepository;

    public RemoveCategoryAttributeUseCase(
        CategoryRepository categoryRepository, 
        CategoryReadRepository categoryReadRepository
    ) {
        this.categoryRepository = categoryRepository;
        this.categoryReadRepository = categoryReadRepository;
    }

    @Override
    public void execute(Id category_id, Id categoryAttribute) {
        Category category = categoryReadRepository.findById(category_id);

        category.removeCategoryAttribute(categoryAttribute);

        categoryRepository.syncCategoryAttributes(category);
    }
}
