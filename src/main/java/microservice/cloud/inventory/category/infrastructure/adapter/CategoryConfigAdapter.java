package microservice.cloud.inventory.category.infrastructure.adapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import microservice.cloud.inventory.attribute.domain.repository.AttributeDefinitionRepository;
import microservice.cloud.inventory.category.application.use_cases.CreateCategoryAttributeUseCase;
import microservice.cloud.inventory.category.application.use_cases.CreateCategoryUseCase;
import microservice.cloud.inventory.category.application.use_cases.DeleteCategoryAttributeUseCase;
import microservice.cloud.inventory.category.application.use_cases.DeleteCategoryUseCase;
import microservice.cloud.inventory.category.application.use_cases.ListCategoryUseCase;
import microservice.cloud.inventory.category.application.use_cases.UpdateCategoryUseCase;
import microservice.cloud.inventory.category.application.ports.out.CategoryReadRepository;
import microservice.cloud.inventory.category.application.use_cases.RollbackCategoryUseCase;
import microservice.cloud.inventory.category.application.use_cases.ListCategoriesByIdsUseCase;
import microservice.cloud.inventory.category.application.use_cases.RealDeleteCategoryUseCase;
import microservice.cloud.inventory.category.domain.repository.CategoryRepository;
import microservice.cloud.inventory.shared.application.ports.out.EventPublisher;
import microservice.cloud.inventory.shared.application.ports.out.GetMePort;

@Configuration
public class CategoryConfigAdapter {

    @Bean
    public ListCategoryUseCase listCategoryUseCase(
        CategoryReadRepository categoryReadRepository
    ) {
        return new ListCategoryUseCase(categoryReadRepository);
    }

    @Bean
    public ListCategoriesByIdsUseCase listCategoriesByIdsUseCase(
        CategoryReadRepository categoryReadRepository 
    ) {
        return new ListCategoriesByIdsUseCase(categoryReadRepository);
    }

    @Bean
    public CreateCategoryUseCase createCategoryUseCase(
        CategoryRepository categoryRepository,
        AttributeDefinitionRepository attributeDefinitionRepository,
        GetMePort getMe
    ) {
        return new CreateCategoryUseCase(
                categoryRepository,
                attributeDefinitionRepository, 
                getMe
            );
    }

    @Bean
    public RollbackCategoryUseCase rollbackCategoryUseCase(
        CategoryRepository categoryRepository
    ) {
        return new RollbackCategoryUseCase(categoryRepository);
    }

    @Bean
    public RealDeleteCategoryUseCase realDeleteCategoryUseCase(
        CategoryRepository categoryRepository
    ) {
        return new RealDeleteCategoryUseCase(categoryRepository);
    }

    @Bean
    public DeleteCategoryUseCase deleteCategoryUseCase(
        CategoryRepository categoryRepository,
        EventPublisher eventPublisher,
        GetMePort getMe
    ) {
        return new DeleteCategoryUseCase(categoryRepository, eventPublisher, getMe);
    }

    @Bean
    public UpdateCategoryUseCase updateCategoryUseCase(
        CategoryRepository categoryRepository,
        EventPublisher eventPublisher,
        GetMePort getMe
    ) {
        return new UpdateCategoryUseCase(categoryRepository, eventPublisher, getMe);
    }

    @Bean
    public CreateCategoryAttributeUseCase createCategoryAttributeUseCase(
        CategoryRepository categoryRepository,
        AttributeDefinitionRepository attributeDefinitionRepository,
        EventPublisher eventPublisher,        
        GetMePort getMe
    ) {
    
        return new CreateCategoryAttributeUseCase(
            categoryRepository, 
            attributeDefinitionRepository,
            eventPublisher,
            getMe
        );
    }

    @Bean
    public DeleteCategoryAttributeUseCase deleteCategoryAttributeUseCase(
        CategoryRepository categoryRepository,
        EventPublisher eventPublisher,
        GetMePort getMe
    ) {
        return new DeleteCategoryAttributeUseCase(
            categoryRepository,
            eventPublisher,
            getMe
        );
    }
}
