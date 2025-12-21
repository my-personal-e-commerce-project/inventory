package microservice.cloud.inventory.category.infrastructure.adapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import microservice.cloud.inventory.category.application.ports.in.CreateCategoryAttributeUseCasePort;
import microservice.cloud.inventory.category.application.ports.in.CreateCategoryUseCasePort;
import microservice.cloud.inventory.category.application.ports.in.DeleteCategoryAttributeUseCasePort;
import microservice.cloud.inventory.category.application.ports.in.DeleteCategoryUseCasePort;
import microservice.cloud.inventory.category.application.ports.in.ListCategoryUseCasePort;
import microservice.cloud.inventory.category.application.ports.in.UpdateCategoryUseCasePort;
import microservice.cloud.inventory.category.application.ports.out.CategoryReadRepository;
import microservice.cloud.inventory.category.application.use_cases.CreateCategoryAttributeUseCase;
import microservice.cloud.inventory.category.application.use_cases.CreateCategoryUseCase;
import microservice.cloud.inventory.category.application.use_cases.DeleteCategoryAttributeUseCase;
import microservice.cloud.inventory.category.application.use_cases.DeleteCategoryUseCase;
import microservice.cloud.inventory.category.application.use_cases.ListCategoryUseCase;
import microservice.cloud.inventory.category.application.use_cases.UpdateCategoryUseCase;
import microservice.cloud.inventory.category.domain.repository.CategoryRepository;
import microservice.cloud.inventory.category.infrastructure.adapter.in.CreateCategoryAttributeUseCaseDispatchEventDecorator;
import microservice.cloud.inventory.category.infrastructure.adapter.in.CreateCategoryUseCaseDispatchEventDecorator;
import microservice.cloud.inventory.category.infrastructure.adapter.in.DeleteCategoryAttributeUseCaseDispatchEventDecorator;
import microservice.cloud.inventory.category.infrastructure.adapter.in.DeleteCategoryUseCaseDispatchEventDecorator;
import microservice.cloud.inventory.category.infrastructure.adapter.in.UpdateCategoryUseCaseDispatchEventDecorator;
import microservice.cloud.inventory.shared.application.ports.in.GetMePort;
import microservice.cloud.inventory.shared.application.ports.out.EventPublishedPort;

@Configuration
public class CategoryConfigAdapter {

    @Bean
    public ListCategoryUseCasePort listCategoryUseCasePort(
        CategoryReadRepository categoryReadRepository
    ) {
        return new ListCategoryUseCase(categoryReadRepository);
    }

    @Bean
    public CreateCategoryUseCasePort createCategoryUseCasePort(
        CategoryRepository categoryRepository,
        GetMePort getMePort,
        EventPublishedPort eventPublishedPort
    ) {
        return new CreateCategoryUseCaseDispatchEventDecorator(
            new CreateCategoryUseCase(categoryRepository, getMePort),
            eventPublishedPort
        );
    }

    @Bean
    public DeleteCategoryUseCasePort deleteCategoryUseCasePort(
        CategoryRepository categoryRepository,
        GetMePort getMePort,
        EventPublishedPort eventPublishedPort
    ) {
        return new DeleteCategoryUseCaseDispatchEventDecorator(
            new DeleteCategoryUseCase(categoryRepository, getMePort),
            eventPublishedPort
        );
    }

    @Bean
    public UpdateCategoryUseCasePort updateCategoryUseCasePort(
        CategoryRepository categoryRepository,
        GetMePort getMePort,
        EventPublishedPort eventPublishedPort
    ) {
        return new UpdateCategoryUseCaseDispatchEventDecorator(
            eventPublishedPort,
            new UpdateCategoryUseCase(categoryRepository, getMePort)
        );
    }

    @Bean
    public CreateCategoryAttributeUseCasePort createCategoryAttributeUseCasePort(
        CategoryRepository categoryRepository,
        GetMePort getMePort,
        EventPublishedPort eventPublishedPort
    ) {
    
        return new CreateCategoryAttributeUseCaseDispatchEventDecorator(
            new CreateCategoryAttributeUseCase(categoryRepository, getMePort),
            eventPublishedPort
        );
    }

    @Bean
    public DeleteCategoryAttributeUseCasePort deleteCategoryAttributeUseCasePort(
        CategoryRepository categoryRepository,
        GetMePort getMePort,
        EventPublishedPort eventPublishedPort
    ) {
        return new DeleteCategoryAttributeUseCaseDispatchEventDecorator(
            new DeleteCategoryAttributeUseCase(
                categoryRepository, 
                getMePort
            ),
            eventPublishedPort
        );
    }
}
