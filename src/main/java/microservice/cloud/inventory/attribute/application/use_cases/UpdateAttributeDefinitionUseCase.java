package microservice.cloud.inventory.attribute.application.use_cases;

import microservice.cloud.inventory.attribute.application.ports.in.UpdateAttributeDefinitionUseCasePort;
import microservice.cloud.inventory.attribute.domain.entity.AttributeDefinition;
import microservice.cloud.inventory.attribute.domain.repository.AttributeDefinitionRepository;
import microservice.cloud.inventory.attribute.domain.value_objects.DataType;
import microservice.cloud.inventory.product.domain.entity.ProductRepository;
import microservice.cloud.inventory.shared.application.ports.out.GetMePort;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;

public class UpdateAttributeDefinitionUseCase implements UpdateAttributeDefinitionUseCasePort {

    private final AttributeDefinitionRepository attributeDefinitionRepository;
    private final ProductRepository productRepository;
    private final GetMePort getMePort;

    public UpdateAttributeDefinitionUseCase(
        AttributeDefinitionRepository attributeDefinitionRepository,
        ProductRepository productRepository,
        GetMePort getMePort
    ) {
        this.attributeDefinitionRepository = attributeDefinitionRepository;
        this.productRepository = productRepository;
        this.getMePort = getMePort;
    }

    @Override
    public AttributeDefinition execute(
        Slug find_slug,
        String name,
        Slug slug,
        DataType type,
        boolean is_global
    ) {
        AttributeDefinition attr = attributeDefinitionRepository.getBySlug(find_slug);

        attr.update(
            getMePort.execute(),
            name,
            slug,
            type,
            is_global
            );

        attributeDefinitionRepository.update(attr);

        productRepository.updateTheValueTypeOfProductAttributesByAttributeDefinition(attr.id(), type);


        return attr;
    } 
}
