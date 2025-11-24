package microservice.cloud.inventory.category.application.use_cases;

import microservice.cloud.inventory.attribute.domain.repository.AttributeDefinitionRepository;
import microservice.cloud.inventory.category.application.ports.in.UpdateCategoryAttributeUseCasePort;
import microservice.cloud.inventory.category.application.ports.out.CategoryReadRepository;
import microservice.cloud.inventory.category.domain.entity.Category;
import microservice.cloud.inventory.category.domain.entity.CategoryAttribute;
import microservice.cloud.inventory.category.domain.repository.CategoryRepository;
import microservice.cloud.inventory.shared.domain.value_objects.Id;

public class UpdateCategoryAttributeUseCase implements UpdateCategoryAttributeUseCasePort {

    private final CategoryRepository categoryRepository;
    private final CategoryReadRepository categoryReadRepository;
    private final AttributeDefinitionRepository attributeDefinitionRepository;

    public UpdateCategoryAttributeUseCase (
        CategoryRepository categoryRepository,
        CategoryReadRepository categoryReadRepository,
        AttributeDefinitionRepository attributeDefinitionRepository
    ) {
        this.categoryRepository = categoryRepository;
        this.categoryReadRepository = categoryReadRepository;
        this.attributeDefinitionRepository = attributeDefinitionRepository;
    }

    @Override
    public void execute(Id categoryId, CategoryAttribute categoryAttribute) {
        Category category = categoryReadRepository.findById(categoryId);

        category.updateCategoryAttribute(categoryAttribute);

        attributeDefinitionRepository.deleteByCategoryAttributeId(categoryAttribute.id());
    
        categoryRepository.update(category);
    } 
}
