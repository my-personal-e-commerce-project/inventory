package microservice.cloud.inventory.category.application.use_cases;

import microservice.cloud.inventory.attribute.domain.entity.AttributeDefinition;
import microservice.cloud.inventory.attribute.domain.repository.AttributeDefinitionRepository;
import microservice.cloud.inventory.category.application.ports.in.CreateCategoryAttributeUseCasePort;
import microservice.cloud.inventory.category.domain.entity.Category;
import microservice.cloud.inventory.category.domain.entity.CategoryAttribute;
import microservice.cloud.inventory.category.domain.repository.CategoryRepository;
import microservice.cloud.inventory.shared.application.ports.out.EventPublisher;
import microservice.cloud.inventory.shared.application.ports.out.GetMePort;
import microservice.cloud.inventory.shared.domain.value_objects.Me;
import microservice.cloud.inventory.shared.domain.value_objects.Permission;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;

public class CreateCategoryAttributeUseCase implements CreateCategoryAttributeUseCasePort {

    private CategoryRepository categoryRepository;
    private AttributeDefinitionRepository attributeDefinitionRepository;
    private EventPublisher eventPublisher;
    private GetMePort getMePort;

    public CreateCategoryAttributeUseCase(
        CategoryRepository categoryRepository,
        AttributeDefinitionRepository attributeDefinitionRepository,
        EventPublisher eventPublisher,
        GetMePort getMePort
    ) {
        this.categoryRepository = categoryRepository;
        this.attributeDefinitionRepository = attributeDefinitionRepository;
        this.eventPublisher = eventPublisher;
        this.getMePort = getMePort;
    }

    public void execute(Slug find_slug, CategoryAttribute categoryAttribute) {
        Me me = getMePort.execute();

        if(me == null)
            throw new RuntimeException("You do not have permission to perform this action.");

        me.IHavePermission(Permission.updateCategory());
        
        Category category = categoryRepository.findBySlug(find_slug);

        AttributeDefinition attrDef = attributeDefinitionRepository.getById(categoryAttribute.attribute_definition_id());

        categoryAttribute.load_attribute_definition(attrDef);

        categoryRepository.updateIfExists(category.id(), (c) -> {
            c.addCategoryAttribute(categoryAttribute);
        });

        if(category.getEvents() != null && !category.getEvents().isEmpty())
            eventPublisher.publish(category.getEvents());
    }
}
