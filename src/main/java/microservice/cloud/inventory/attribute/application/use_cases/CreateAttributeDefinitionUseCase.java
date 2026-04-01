package microservice.cloud.inventory.attribute.application.use_cases;

import microservice.cloud.inventory.attribute.application.ports.in.CreateAttributeDefinitionUseCasePort;
import microservice.cloud.inventory.attribute.domain.entity.AttributeDefinition;
import microservice.cloud.inventory.attribute.domain.repository.AttributeDefinitionRepository;
import microservice.cloud.inventory.attribute.domain.value_objects.DataType;
import microservice.cloud.inventory.product.domain.entity.ProductRepository;
import microservice.cloud.inventory.shared.application.ports.out.GetMePort;
import microservice.cloud.inventory.shared.domain.value_objects.Id;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;

public class CreateAttributeDefinitionUseCase implements CreateAttributeDefinitionUseCasePort {

    private AttributeDefinitionRepository attributeDefinitionRepository;
    private ProductRepository productRepository;
    private GetMePort getMePort;

    public CreateAttributeDefinitionUseCase(
        AttributeDefinitionRepository attributeDefinitionRepository,
        ProductRepository productRepository,
        GetMePort getMePort
    ) {
        this.attributeDefinitionRepository = attributeDefinitionRepository;
        this.productRepository = productRepository;
        this.getMePort = getMePort;
    }

    @Override
    public void execute(
        Id id,
        String name,
        Slug slug,
        DataType type,
        boolean is_global
    ) {
        AttributeDefinition attr = AttributeDefinition.factory(
            getMePort.execute(), 
            id, 
            name, 
            slug, 
            type, 
            is_global
        );

        if(is_global)
            productRepository.massCreateDefaultProductAttributeValues(attr);

        attributeDefinitionRepository.save(attr);
    }
}
