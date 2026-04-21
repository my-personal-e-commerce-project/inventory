package microservice.cloud.inventory.attribute.application.use_cases;

import microservice.cloud.inventory.attribute.application.ports.in.UpdateAttributeDefinitionUseCasePort;
import microservice.cloud.inventory.attribute.domain.entity.AttributeDefinition;
import microservice.cloud.inventory.attribute.domain.repository.AttributeDefinitionRepository;
import microservice.cloud.inventory.attribute.domain.value_objects.DataType;
import microservice.cloud.inventory.shared.application.ports.out.GetMePort;
import microservice.cloud.inventory.shared.domain.value_objects.Me;
import microservice.cloud.inventory.shared.domain.value_objects.Permission;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;

public class UpdateAttributeDefinitionUseCase implements UpdateAttributeDefinitionUseCasePort {

    private final AttributeDefinitionRepository attributeDefinitionRepository;
    private final GetMePort getMePort;

    public UpdateAttributeDefinitionUseCase(
        AttributeDefinitionRepository attributeDefinitionRepository,
        GetMePort getMePort
    ) {
        this.attributeDefinitionRepository = attributeDefinitionRepository;
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
        Me me = getMePort.execute();

        if(me == null)
            throw new RuntimeException("You do not have permission to perform this action");

        me.IHavePermission(Permission.updateAttributeDefinition());

        AttributeDefinition attr = attributeDefinitionRepository.getBySlug(find_slug);

        attr.update(
            name,
            slug,
            type,
            is_global
            );

        attributeDefinitionRepository.update(attr);

        return attr;
    } 
}
