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
        EventPublishedPort eventPublishedPort,
        GetMePort getMePort
    ) {
        return new CreateCategoryUseCase(categoryRepository, eventPublishedPort, getMePort);
    }

    @Bean
    public DeleteCategoryUseCasePort deleteCategoryUseCasePort(
        CategoryRepository categoryRepository,
        EventPublishedPort eventPublishedPort,
        GetMePort getMePort
    ) {
        return new DeleteCategoryUseCase(categoryRepository, eventPublishedPort, getMePort);
    }

    @Bean
    public UpdateCategoryUseCasePort updateCategoryUseCasePort(
        CategoryRepository categoryRepository,
        EventPublishedPort eventPublishedPort,
        GetMePort getMePort
    ) {
        return new UpdateCategoryUseCase(categoryRepository, eventPublishedPort, getMePort);
    }

    @Bean
    public CreateCategoryAttributeUseCasePort createCategoryAttributeUseCasePort(
        CategoryRepository categoryRepository,
        CategoryReadRepository categoryReadRepository,
        EventPublishedPort eventPublishedPort,
        GetMePort getMePort
    ) {
    
        return new CreateCategoryAttributeUseCase(categoryRepository, categoryReadRepository, eventPublishedPort, getMePort);
    }

    @Bean
    public DeleteCategoryAttributeUseCasePort deleteCategoryAttributeUseCasePort(
        CategoryRepository categoryRepository,
        CategoryReadRepository categoryReadRepository,
        EventPublishedPort eventPublishedPort,
        GetMePort getMePort
    ) {
        return new DeleteCategoryAttributeUseCase(
            categoryRepository, 
            categoryReadRepository,
            eventPublishedPort,
            getMePort
        );
    }
}
