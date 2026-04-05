package microservice.cloud.inventory.category.application.use_cases;

import microservice.cloud.inventory.attribute.domain.entity.AttributeDefinition;
import microservice.cloud.inventory.attribute.domain.repository.AttributeDefinitionRepository;
import microservice.cloud.inventory.category.application.ports.in.CreateCategoryAttributeUseCasePort;
import microservice.cloud.inventory.category.domain.entity.Category;
import microservice.cloud.inventory.category.domain.entity.CategoryAttribute;
import microservice.cloud.inventory.category.domain.repository.CategoryRepository;
import microservice.cloud.inventory.product.domain.entity.ProductRepository;
import microservice.cloud.inventory.shared.application.ports.out.GetMePort;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;

public class CreateCategoryAttributeUseCase implements CreateCategoryAttributeUseCasePort {

    private CategoryRepository categoryRepository;
    private AttributeDefinitionRepository attributeDefinitionRepository;
    private ProductRepository productRepository;
    private GetMePort getMePort;

    public CreateCategoryAttributeUseCase(
        CategoryRepository categoryRepository,
        AttributeDefinitionRepository attributeDefinitionRepository,
        ProductRepository productRepository,
        GetMePort getMePort
    ) {
        this.categoryRepository = categoryRepository;
        this.attributeDefinitionRepository = attributeDefinitionRepository;
        this.productRepository = productRepository;
        this.getMePort = getMePort;
    }

    public Category execute(Slug find_slug, CategoryAttribute categoryAttribute) {
        Category category = categoryRepository.findBySlug(find_slug);

        AttributeDefinition attrDef = attributeDefinitionRepository.getById(categoryAttribute.attribute_definition_id());

        categoryAttribute.load_attribute_definition(attrDef);

        category.addCategoryAttribute(getMePort.execute(), categoryAttribute);

        categoryRepository.update(category);

        if(categoryAttribute.is_required())
            productRepository.massCreateProductAttributeValuesByCategory(category.id(), attrDef);

        return category;
    }
}
