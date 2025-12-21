package microservice.cloud.inventory.attribute.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import microservice.cloud.inventory.attribute.application.ports.in.CreateAttributeDefinitionUseCasePort;
import microservice.cloud.inventory.attribute.application.ports.in.DeleteAttributeDefinitionUseCasePort;
import microservice.cloud.inventory.attribute.application.ports.in.ListAttributeDefinitionUseCasePort;
import microservice.cloud.inventory.attribute.application.ports.in.UpdateAttributeDefinitionUseCasePort;
import microservice.cloud.inventory.attribute.application.ports.out.AttributeDefinitionReadRepository;
import microservice.cloud.inventory.attribute.application.use_cases.CreateAttributeDefinitionUseCase;
import microservice.cloud.inventory.attribute.application.use_cases.DeleteAttributeDefinitionUseCase;
import microservice.cloud.inventory.attribute.application.use_cases.ListAttributeDefinitionUseCase;
import microservice.cloud.inventory.attribute.application.use_cases.UpdateAttributeDefinitionUseCase;
import microservice.cloud.inventory.attribute.domain.repository.AttributeDefinitionRepository;

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
        AttributeDefinitionRepository attributeDefinitionRepository
    ) {
        return new CreateAttributeDefinitionUseCase(attributeDefinitionRepository);
    }

    @Bean
    public UpdateAttributeDefinitionUseCasePort UpdateAttributeDefinitionUseCase(
        AttributeDefinitionRepository attributeDefinitionRepository
    ) {
        return new UpdateAttributeDefinitionUseCase(attributeDefinitionRepository);
    }
    
    @Bean
    public DeleteAttributeDefinitionUseCasePort deleteAttributeDefinitionUseCasePort(
        AttributeDefinitionRepository attributeDefinitionRepository
    ) {
        return new DeleteAttributeDefinitionUseCase(attributeDefinitionRepository);
    }
}
