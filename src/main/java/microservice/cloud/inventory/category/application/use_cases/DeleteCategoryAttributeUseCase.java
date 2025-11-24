package microservice.cloud.inventory.category.application.use_cases;

import microservice.cloud.inventory.attribute.domain.repository.AttributeDefinitionRepository;
import microservice.cloud.inventory.category.application.ports.in.DeleteCategoryAttributeUseCasePort;
import microservice.cloud.inventory.category.application.ports.out.CategoryReadRepository;
import microservice.cloud.inventory.category.domain.entity.Category;
import microservice.cloud.inventory.category.domain.repository.CategoryRepository;
import microservice.cloud.inventory.shared.domain.value_objects.Id;

public class DeleteCategoryAttributeUseCase implements DeleteCategoryAttributeUseCasePort {

    private CategoryRepository categoryRepository;
    private CategoryReadRepository categoryReadRepository;
    private AttributeDefinitionRepository attributeDefinitionRepository;

    public DeleteCategoryAttributeUseCase(
        CategoryRepository categoryRepository,
        AttributeDefinitionRepository attributeDefinitionRepository,
        CategoryReadRepository categoryReadRepository
    ) {
    
        this.categoryRepository = categoryRepository;
        this.attributeDefinitionRepository = attributeDefinitionRepository;
        this.categoryReadRepository = categoryReadRepository;
    }
    
    public void execute(Id categoryId, Id categoryAttributeId) {
        Category category = categoryReadRepository.findById(categoryId);

        category.removeCategoryAttribute(categoryAttributeId);

        attributeDefinitionRepository.deleteByCategoryAttributeId(categoryAttributeId);

        categoryRepository.update(category);
    }
}
