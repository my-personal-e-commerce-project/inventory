package microservice.cloud.inventory.attribute.application.use_cases;

import microservice.cloud.inventory.attribute.application.ports.in.CreateAttributeDefinitionUseCasePort;
import microservice.cloud.inventory.attribute.domain.entity.AttributeDefinition;
import microservice.cloud.inventory.attribute.domain.repository.AttributeDefinitionRepository;
import microservice.cloud.inventory.attribute.domain.value_objects.DataType;
import microservice.cloud.inventory.shared.application.ports.out.GetMePort;
import microservice.cloud.inventory.shared.domain.value_objects.Id;
import microservice.cloud.inventory.shared.domain.value_objects.Me;
import microservice.cloud.inventory.shared.domain.value_objects.Permission;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;

import microservice.cloud.inventory.shared.application.ports.out.EventPublisher;

public class CreateAttributeDefinitionUseCase implements CreateAttributeDefinitionUseCasePort {

    private AttributeDefinitionRepository attributeDefinitionRepository;
    private EventPublisher eventPublisher;
    private GetMePort getMePort;

    public CreateAttributeDefinitionUseCase(
        AttributeDefinitionRepository attributeDefinitionRepository,
        EventPublisher eventPublisher,
        GetMePort getMePort
    ) {
        this.attributeDefinitionRepository = attributeDefinitionRepository;
        this.eventPublisher = eventPublisher;
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
        Me me = getMePort.execute();

        if(me == null)
            throw new RuntimeException("You do not have permission to perform this action");

        me.IHavePermission(Permission.createAttributeDefinition());

        AttributeDefinition attrDef = AttributeDefinition.factory(
            id, 
            name, 
            slug, 
            type, 
            is_global
        );

        attributeDefinitionRepository.save(
            attrDef
        );
        
        eventPublisher.publish(attrDef.getEvents());
    }
}
