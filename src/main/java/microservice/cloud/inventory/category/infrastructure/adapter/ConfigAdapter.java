package microservice.cloud.inventory.category.infrastructure.adapter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import microservice.cloud.inventory.category.application.ports.in.CreateAttributeUseCasePort;
import microservice.cloud.inventory.category.application.ports.in.DeleteAttributeUseCasePort;
import microservice.cloud.inventory.category.application.ports.in.ListAttributesUseCasePort;
import microservice.cloud.inventory.category.application.ports.in.UpdateAttributeUseCasePort;
import microservice.cloud.inventory.category.application.ports.out.AttributeReadRepository;
import microservice.cloud.inventory.category.application.use_cases.CreateAttributeUseCase;
import microservice.cloud.inventory.category.application.use_cases.DeleteAttributeUseCase;
import microservice.cloud.inventory.category.application.use_cases.ListAttributeUseCase;
import microservice.cloud.inventory.category.application.use_cases.UpdateAttributeUseCase;
import microservice.cloud.inventory.category.domain.repository.CategoryAttributeRepository;

@Configuration
public class ConfigAdapter {

    @Bean
    public UpdateAttributeUseCasePort updateAttributeUseCasePort(
        CategoryAttributeRepository categoryAttributeRepository
    ) {
        return new UpdateAttributeUseCase(categoryAttributeRepository);
    }

    @Bean
    public DeleteAttributeUseCasePort deleteAttributeUseCasePort(
        CategoryAttributeRepository categoryAttributeRepository
    ) {
        return new DeleteAttributeUseCase(categoryAttributeRepository);
    }

    @Bean
    public CreateAttributeUseCasePort createAttributeUseCasePort(
        CategoryAttributeRepository categoryAttributeRepository
    ) {
        return new CreateAttributeUseCase(categoryAttributeRepository);
    }

    @Bean
    public ListAttributesUseCasePort listAttributesUseCasePort(
        AttributeReadRepository attributeReadRepository       
    ) {
        return new ListAttributeUseCase(attributeReadRepository);
    }
}
