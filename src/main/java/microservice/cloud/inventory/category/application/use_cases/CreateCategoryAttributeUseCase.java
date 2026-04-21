package microservice.cloud.inventory.category.application.use_cases;

import java.util.List;

import microservice.cloud.inventory.attribute.domain.entity.AttributeDefinition;
import microservice.cloud.inventory.attribute.domain.repository.AttributeDefinitionRepository;
import microservice.cloud.inventory.category.application.ports.in.CreateCategoryAttributeUseCasePort;
import microservice.cloud.inventory.category.application.ports.out.CreateProductAttributeValuesInBulkForNewRequiredCategoryAttributesAsynchronously;
import microservice.cloud.inventory.category.domain.entity.Category;
import microservice.cloud.inventory.category.domain.entity.CategoryAttribute;
import microservice.cloud.inventory.category.domain.repository.CategoryRepository;
import microservice.cloud.inventory.shared.application.ports.out.EventPublisher;
import microservice.cloud.inventory.shared.application.ports.out.GetMePort;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;

public class CreateCategoryAttributeUseCase implements CreateCategoryAttributeUseCasePort {

    private CategoryRepository categoryRepository;
    private AttributeDefinitionRepository attributeDefinitionRepository;
    private CreateProductAttributeValuesInBulkForNewRequiredCategoryAttributesAsynchronously createProductAttributeValuesInBulkForNewRequiredCategoryAttributesAsynchronously;
    private EventPublisher eventPublisher;
    private GetMePort getMePort;

    public CreateCategoryAttributeUseCase(
        CategoryRepository categoryRepository,
        AttributeDefinitionRepository attributeDefinitionRepository,
        CreateProductAttributeValuesInBulkForNewRequiredCategoryAttributesAsynchronously createProductAttributeValuesInBulkForNewRequiredCategoryAttributesAsynchronously,
        EventPublisher eventPublisher,
        GetMePort getMePort
    ) {
        this.categoryRepository = categoryRepository;
        this.attributeDefinitionRepository = attributeDefinitionRepository;
        this.createProductAttributeValuesInBulkForNewRequiredCategoryAttributesAsynchronously = createProductAttributeValuesInBulkForNewRequiredCategoryAttributesAsynchronously;
        this.eventPublisher = eventPublisher;
        this.getMePort = getMePort;
    }

    public void execute(Slug find_slug, CategoryAttribute categoryAttribute) {
        Category category = categoryRepository.findBySlug(find_slug);

        AttributeDefinition attrDef = attributeDefinitionRepository.getById(categoryAttribute.attribute_definition_id());

        categoryAttribute.load_attribute_definition(attrDef);

        category.addCategoryAttribute(getMePort.execute(), categoryAttribute);

        categoryRepository.update(category);

        if(categoryAttribute.is_required())
            createProductAttributeValuesInBulkForNewRequiredCategoryAttributesAsynchronously
                .execute(category.id(), List.of(categoryAttribute));

        eventPublisher.publish(category.getEvents());
    }
}
