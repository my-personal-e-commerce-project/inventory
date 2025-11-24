package microservice.cloud.inventory.category.application.use_cases;

import microservice.cloud.inventory.category.application.ports.in.CreateCategoryAttributeUseCasePort;
import microservice.cloud.inventory.category.application.ports.out.CategoryReadRepository;
import microservice.cloud.inventory.category.domain.entity.Category;
import microservice.cloud.inventory.category.domain.entity.CategoryAttribute;
import microservice.cloud.inventory.category.domain.repository.CategoryRepository;
import microservice.cloud.inventory.shared.domain.value_objects.Id;

public class CreateCategoryAttributeUseCase implements CreateCategoryAttributeUseCasePort {

    private CategoryRepository categoryRepository;
    private CategoryReadRepository categoryReadRepository;

    public CreateCategoryAttributeUseCase(
        CategoryRepository categoryRepository,
        CategoryReadRepository categoryReadRepository
    ) {
        this.categoryRepository = categoryRepository;
        this.categoryReadRepository = categoryReadRepository;
    }

    public void execute(Id categoryId, CategoryAttribute categoryAttribute) {
        Category category = categoryReadRepository.findById(categoryId);

        category.addCategoryAttribute(categoryAttribute);

        categoryRepository.update(category);
    }
}
