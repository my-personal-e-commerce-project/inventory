package microservice.cloud.inventory.category.application.use_cases;

import microservice.cloud.inventory.category.application.ports.in.AddCategoryAttributeUseCasePort;
import microservice.cloud.inventory.category.application.ports.out.CategoryReadRepository;
import microservice.cloud.inventory.category.domain.entity.Category;
import microservice.cloud.inventory.category.domain.entity.CategoryAttribute;
import microservice.cloud.inventory.category.domain.repository.CategoryRepository;
import microservice.cloud.inventory.shared.domain.value_objects.Id;

public class AddCategoryAttributeUseCase implements AddCategoryAttributeUseCasePort {

    private CategoryRepository categoryRepository;
    private CategoryReadRepository categoryReadRepository;

    public AddCategoryAttributeUseCase(
        CategoryRepository categoryRepository,
        CategoryReadRepository categoryReadRepository
    ) {
        this.categoryRepository = categoryRepository;
        this.categoryReadRepository = categoryReadRepository;
    }

    @Override
    public void execute(Id id, CategoryAttribute categoryAttribute) {
        Category category = categoryReadRepository.findById(id);

        category.addCategoryAttribute(categoryAttribute);

        categoryRepository.syncCategoryAttributes(category);
    }
}
