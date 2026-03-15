package microservice.cloud.inventory.category.application.use_cases;

import java.util.List;
import java.util.Map;

import microservice.cloud.inventory.category.application.ports.in.CreateCategoryUseCasePort;
import microservice.cloud.inventory.category.domain.entity.Category;
import microservice.cloud.inventory.category.domain.entity.CategoryAttribute;
import microservice.cloud.inventory.category.domain.repository.CategoryRepository;
import microservice.cloud.inventory.shared.application.ports.out.GetMePort;
import microservice.cloud.inventory.shared.domain.exception.DataNotFound;
import microservice.cloud.inventory.shared.domain.value_objects.Id;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;
import microservice.cloud.inventory.attribute.domain.entity.AttributeDefinition;
import microservice.cloud.inventory.attribute.domain.repository.AttributeDefinitionRepository;

public class CreateCategoryUseCase implements CreateCategoryUseCasePort {

    private CategoryRepository categoryRepository;
    private AttributeDefinitionRepository attributeDefinitionRepository;
    private GetMePort getMePort;

    public CreateCategoryUseCase(
        CategoryRepository categoryRepository,
        AttributeDefinitionRepository attributeDefinitionRepository,
        GetMePort getMePort
    ) {
        this.categoryRepository = categoryRepository;
        this.attributeDefinitionRepository = attributeDefinitionRepository;
        this.getMePort = getMePort;
    }

    private void loadAttributeDefinitions(List<CategoryAttribute> attributes){
        List<String> ids = attributes.stream()
            .map(attr -> attr.attribute_definition_id().value())
            .toList();

        Map<String, AttributeDefinition> definitionsMap = attributeDefinitionRepository.findByIds(ids);

        attributes.forEach(attr -> {
            AttributeDefinition attrDef = definitionsMap.get(attr.attribute_definition_id().value());

            if(attrDef == null) {
                throw new DataNotFound("Category attribute with id " + attr.attribute_definition_id().value() + " not found");
            }

            attr.load_attribute_definition(
                attrDef
            );
        });
    }

    @Override
    public Category execute(
       Id id,
       String name,
       Slug slug,
       Id parent_id,
       List<CategoryAttribute> attributes
    ) {
        loadAttributeDefinitions(attributes);

        Category category = Category
            .factory(getMePort.execute(), id, name, slug, parent_id, attributes);

        categoryRepository.save(category);

        return category;
    }
}
