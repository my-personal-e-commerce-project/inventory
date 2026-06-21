package microservice.cloud.inventory.attribute.application.use_cases;

import microservice.cloud.inventory.attribute.domain.entity.AttributeDefinition;
import microservice.cloud.inventory.attribute.domain.repository.AttributeDefinitionRepository;
import microservice.cloud.inventory.shared.application.ports.out.GetMePort;
import microservice.cloud.inventory.shared.domain.value_objects.Me;
import microservice.cloud.inventory.shared.domain.value_objects.Permission;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;

public class DeleteAttributeDefinitionUseCase {

    private final AttributeDefinitionRepository attributeDefinitionRepository;
    private final GetMePort getMePort;
        
    public DeleteAttributeDefinitionUseCase(
        AttributeDefinitionRepository attributeDefinitionRepository,
        GetMePort getMePort
    ) {
        this.attributeDefinitionRepository = attributeDefinitionRepository;
        this.getMePort = getMePort;
    }

    public void execute(Slug find_slug) {
        Me me = getMePort.execute();

        if(me == null)
            throw new RuntimeException("You do not have permission to perform this action");

        me.IHavePermission(Permission.deleteAttributeDefinition());

        AttributeDefinition attrDef = attributeDefinitionRepository.getBySlug(find_slug);

        attributeDefinitionRepository.delete(attrDef);
    } 
}
