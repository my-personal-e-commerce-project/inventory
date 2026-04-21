package microservice.cloud.inventory.attribute.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import microservice.cloud.inventory.attribute.application.ports.in.CreateAttributeDefinitionUseCasePort;
import microservice.cloud.inventory.attribute.application.ports.in.DeleteAttributeDefinitionUseCasePort;
import microservice.cloud.inventory.attribute.application.ports.in.ListAttributeDefinitionUseCasePort;
import microservice.cloud.inventory.attribute.application.ports.in.UpdateAttributeDefinitionUseCasePort;
import microservice.cloud.inventory.attribute.application.ports.out.AsynchronousBulkCreationOfDefaultValuesForProductAttributes;
import microservice.cloud.inventory.attribute.application.ports.out.AttributeDefinitionReadRepository;
import microservice.cloud.inventory.attribute.application.use_cases.CreateAttributeDefinitionUseCase;
import microservice.cloud.inventory.attribute.application.use_cases.DeleteAttributeDefinitionUseCase;
import microservice.cloud.inventory.attribute.application.use_cases.ListAttributeDefinitionUseCase;
import microservice.cloud.inventory.attribute.application.use_cases.UpdateAttributeDefinitionUseCase;
import microservice.cloud.inventory.attribute.domain.repository.AttributeDefinitionRepository;
import microservice.cloud.inventory.shared.application.ports.out.EventPublisher;
import microservice.cloud.inventory.shared.application.ports.out.GetMePort;

@Configuration
public class AttributeDefinitionConfig {

    @Bean
    public ListAttributeDefinitionUseCasePort listAttributeDefinitionUseCasePort(
        AttributeDefinitionReadRepository attributeDefinitionReadRepository
    ) {
        return new ListAttributeDefinitionUseCase(attributeDefinitionReadRepository);
    }

    @Bean
    public CreateAttributeDefinitionUseCasePort createAttributeDefinitionUseCasePort(
        AttributeDefinitionRepository attributeDefinitionRepository,
        AsynchronousBulkCreationOfDefaultValuesForProductAttributes asynchronousBulkCreationOfDefaultValuesForProductAttributes,
        EventPublisher eventPublisher,
        GetMePort getMePort
    ) {
        return new CreateAttributeDefinitionUseCase(
            attributeDefinitionRepository, 
            asynchronousBulkCreationOfDefaultValuesForProductAttributes, 
            eventPublisher, 
            getMePort
        );
    }

    @Bean
    public UpdateAttributeDefinitionUseCasePort UpdateAttributeDefinitionUseCase(
        AttributeDefinitionRepository attributeDefinitionRepository,
        GetMePort getMePort
    ) {
        return new UpdateAttributeDefinitionUseCase(attributeDefinitionRepository, getMePort);
    }
    
    @Bean
    public DeleteAttributeDefinitionUseCasePort deleteAttributeDefinitionUseCasePort(
        AttributeDefinitionRepository attributeDefinitionRepository,
        GetMePort getMePort
    ) {
        return new DeleteAttributeDefinitionUseCase(attributeDefinitionRepository, getMePort);
    }
}
