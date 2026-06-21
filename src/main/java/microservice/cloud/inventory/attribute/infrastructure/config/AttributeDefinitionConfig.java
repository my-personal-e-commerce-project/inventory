package microservice.cloud.inventory.attribute.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import microservice.cloud.inventory.attribute.application.use_cases.CreateAttributeDefinitionUseCase;
import microservice.cloud.inventory.attribute.application.use_cases.DeleteAttributeDefinitionUseCase;
import microservice.cloud.inventory.attribute.application.use_cases.ListAttributeDefinitionUseCase;
import microservice.cloud.inventory.attribute.application.ports.out.AttributeDefinitionReadRepository;
import microservice.cloud.inventory.attribute.application.use_cases.UpdateAttributeDefinitionUseCase;
import microservice.cloud.inventory.attribute.domain.repository.AttributeDefinitionRepository;
import microservice.cloud.inventory.shared.application.ports.out.EventPublisher;
import microservice.cloud.inventory.shared.application.ports.out.GetMePort;

@Configuration
public class AttributeDefinitionConfig {

    @Bean
    public ListAttributeDefinitionUseCase listAttributeDefinitionUseCase(
        AttributeDefinitionReadRepository attributeDefinitionReadRepository
    ) {
        return new ListAttributeDefinitionUseCase(attributeDefinitionReadRepository);
    }

    @Bean
    public CreateAttributeDefinitionUseCase createAttributeDefinitionUseCase(
        AttributeDefinitionRepository attributeDefinitionRepository,
        EventPublisher eventPublisher,
        GetMePort getMe
    ) {
        return new CreateAttributeDefinitionUseCase(
            attributeDefinitionRepository, 
            eventPublisher, 
            getMe
        );
    }

    @Bean
    public UpdateAttributeDefinitionUseCase UpdateAttributeDefinitionUseCase(
        AttributeDefinitionRepository attributeDefinitionRepository,
        GetMePort getMe
    ) {
        return new UpdateAttributeDefinitionUseCase(attributeDefinitionRepository, getMe);
    }
    
    @Bean
    public DeleteAttributeDefinitionUseCase deleteAttributeDefinitionUseCase(
        AttributeDefinitionRepository attributeDefinitionRepository,
        GetMePort getMe
    ) {
        return new DeleteAttributeDefinitionUseCase(attributeDefinitionRepository, getMe);
    }
}
