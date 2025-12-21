package microservice.cloud.inventory.category.application.use_cases;

import microservice.cloud.inventory.category.application.ports.in.CreateCategoryAttributeUseCasePort;
import microservice.cloud.inventory.category.domain.entity.Category;
import microservice.cloud.inventory.category.domain.entity.CategoryAttribute;
import microservice.cloud.inventory.category.domain.repository.CategoryRepository;
import microservice.cloud.inventory.shared.application.ports.in.GetMePort;
import microservice.cloud.inventory.shared.domain.value_objects.Id;

public class CreateCategoryAttributeUseCase implements CreateCategoryAttributeUseCasePort {

    private CategoryRepository categoryRepository;
    private GetMePort getMePort;

    public CreateCategoryAttributeUseCase(
        CategoryRepository categoryRepository,
        GetMePort getMePort
    ) {
        this.categoryRepository = categoryRepository;
        this.getMePort = getMePort;
    }

    public Category execute(Id categoryId, CategoryAttribute categoryAttribute) {
        Category category = categoryRepository.findById(categoryId);

        category.addCategoryAttribute(getMePort.execute(), categoryAttribute);

        categoryRepository.update(category);

        return category;
    }
}
