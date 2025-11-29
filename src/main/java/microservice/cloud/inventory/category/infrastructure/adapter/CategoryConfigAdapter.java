package microservice.cloud.inventory.category.infrastructure.adapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import microservice.cloud.inventory.attribute.domain.repository.AttributeDefinitionRepository;
import microservice.cloud.inventory.category.application.ports.in.CreateCategoryAttributeUseCasePort;
import microservice.cloud.inventory.category.application.ports.in.CreateCategoryUseCasePort;
import microservice.cloud.inventory.category.application.ports.in.DeleteCategoryAttributeUseCasePort;
import microservice.cloud.inventory.category.application.ports.in.DeleteCategoryUseCasePort;
import microservice.cloud.inventory.category.application.ports.in.ListCategoryUseCasePort;
import microservice.cloud.inventory.category.application.ports.in.UpdateCategoryAttributeUseCasePort;
import microservice.cloud.inventory.category.application.ports.in.UpdateCategoryUseCasePort;
import microservice.cloud.inventory.category.application.ports.out.CategoryReadRepository;
import microservice.cloud.inventory.category.application.use_cases.CreateCategoryAttributeUseCase;
import microservice.cloud.inventory.category.application.use_cases.CreateCategoryUseCase;
import microservice.cloud.inventory.category.application.use_cases.DeleteCategoryAttributeUseCase;
import microservice.cloud.inventory.category.application.use_cases.DeleteCategoryUseCase;
import microservice.cloud.inventory.category.application.use_cases.ListCategoryUseCase;
import microservice.cloud.inventory.category.application.use_cases.UpdateCategoryAttributeUseCase;
import microservice.cloud.inventory.category.application.use_cases.UpdateCategoryUseCase;
import microservice.cloud.inventory.category.domain.repository.CategoryRepository;

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
        CategoryRepository categoryRepository
    ) {
        return new CreateCategoryUseCase(categoryRepository);
    }

    @Bean
    public DeleteCategoryUseCasePort deleteCategoryUseCasePort(
        CategoryRepository categoryRepository
    ) {
        return new DeleteCategoryUseCase(categoryRepository);
    }

    @Bean
    public UpdateCategoryUseCasePort updateCategoryUseCasePort(
        CategoryRepository categoryRepository
    ) {
        return new UpdateCategoryUseCase(categoryRepository);
    }

    @Bean
    public UpdateCategoryAttributeUseCasePort updateCategoryAttributeUseCasePort(
        CategoryRepository categoryRepository,
        CategoryReadRepository categoryReadRepository,
        AttributeDefinitionRepository attributeDefinitionRepository
    ) {
        return new UpdateCategoryAttributeUseCase(
                categoryRepository, 
                categoryReadRepository, 
                attributeDefinitionRepository
            );
    }

    @Bean
    public CreateCategoryAttributeUseCasePort createCategoryAttributeUseCasePort(
        CategoryRepository categoryRepository,
        CategoryReadRepository categoryReadRepository
    ) {
    
        return new CreateCategoryAttributeUseCase(categoryRepository, categoryReadRepository);
    }

    @Bean
    public DeleteCategoryAttributeUseCasePort deleteCategoryAttributeUseCasePort(
        CategoryRepository categoryRepository,
        CategoryReadRepository categoryReadRepository,
        AttributeDefinitionRepository attributeDefinitionRepository
    ) {
        return new DeleteCategoryAttributeUseCase(
            categoryRepository, 
            attributeDefinitionRepository, 
            categoryReadRepository
        );
    }
}
