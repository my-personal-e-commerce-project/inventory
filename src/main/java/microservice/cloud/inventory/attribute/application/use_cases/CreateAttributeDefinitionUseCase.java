package microservice.cloud.inventory.attribute.application.use_cases;

import microservice.cloud.inventory.attribute.application.ports.in.CreateAttributeDefinitionUseCasePort;
import microservice.cloud.inventory.attribute.domain.entity.AttributeDefinition;
import microservice.cloud.inventory.attribute.domain.repository.AttributeDefinitionRepository;
import microservice.cloud.inventory.product.domain.entity.ProductRepository;
import microservice.cloud.inventory.shared.application.ports.out.GetMePort;
import microservice.cloud.inventory.shared.domain.value_objects.Me;
import microservice.cloud.inventory.shared.domain.value_objects.Permission;

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
        AttributeDefinition attributeDefinition
    ) {
        Me me = getMePort.execute();

        if(me == null)
            throw new RuntimeException("You do not have permission to perform this action");

        me.IHavePermission(Permission.createAttributeDefinition());

        if(attributeDefinition.is_global())
            productRepository.massCreateDefaultProductAttributeValues(attributeDefinition);

        attributeDefinitionRepository.save(attributeDefinition);
    }
}
