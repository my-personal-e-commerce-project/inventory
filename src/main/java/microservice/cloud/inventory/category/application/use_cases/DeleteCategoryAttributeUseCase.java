package microservice.cloud.inventory.category.application.use_cases;

import microservice.cloud.inventory.category.application.ports.in.DeleteCategoryAttributeUseCasePort;
import microservice.cloud.inventory.category.domain.entity.Category;
import microservice.cloud.inventory.category.domain.repository.CategoryRepository;
import microservice.cloud.inventory.shared.application.ports.in.GetMePort;
import microservice.cloud.inventory.shared.domain.value_objects.Id;

public class DeleteCategoryAttributeUseCase implements DeleteCategoryAttributeUseCasePort {

    private CategoryRepository categoryRepository;
    private GetMePort getMePort;

    public DeleteCategoryAttributeUseCase(
        CategoryRepository categoryRepository,
        GetMePort getMePort
    ) {
        this.categoryRepository = categoryRepository;
        this.getMePort = getMePort;
    }
    
    public Category execute(Id categoryId, Id categoryAttributeId) {
        Category category = categoryRepository.findById(categoryId);

        category.removeCategoryAttribute(getMePort.execute(), categoryAttributeId);

        categoryRepository.update(category);

        return category;
    }
}
