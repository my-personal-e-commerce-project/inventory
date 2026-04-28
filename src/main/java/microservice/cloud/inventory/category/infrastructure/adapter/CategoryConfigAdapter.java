package microservice.cloud.inventory.category.infrastructure.adapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import microservice.cloud.inventory.attribute.domain.repository.AttributeDefinitionRepository;
import microservice.cloud.inventory.category.application.ports.in.CreateCategoryAttributeUseCasePort;
import microservice.cloud.inventory.category.application.ports.in.CreateCategoryUseCasePort;
import microservice.cloud.inventory.category.application.ports.in.DeleteCategoryAttributeUseCasePort;
import microservice.cloud.inventory.category.application.ports.in.DeleteCategoryUseCasePort;
import microservice.cloud.inventory.category.application.ports.in.ListCategoryUseCasePort;
import microservice.cloud.inventory.category.application.ports.in.UpdateCategoryUseCasePort;
import microservice.cloud.inventory.category.application.ports.out.CategoryReadRepository;
import microservice.cloud.inventory.category.application.ports.out.CreateProductAttributeValuesInBulkForNewRequiredCategoryAttributesAsynchronously;
import microservice.cloud.inventory.category.application.use_cases.CreateCategoryAttributeUseCase;
import microservice.cloud.inventory.category.application.use_cases.CreateCategoryUseCase;
import microservice.cloud.inventory.category.application.use_cases.DeleteCategoryAttributeUseCase;
import microservice.cloud.inventory.category.application.use_cases.DeleteCategoryUseCase;
import microservice.cloud.inventory.category.application.use_cases.ListCategoriesByIdsUseCase;
import microservice.cloud.inventory.category.application.use_cases.ListCategoryUseCase;
import microservice.cloud.inventory.category.application.use_cases.UpdateCategoryUseCase;
import microservice.cloud.inventory.category.domain.repository.CategoryRepository;
import microservice.cloud.inventory.shared.application.ports.out.EventPublisher;
import microservice.cloud.inventory.shared.application.ports.out.GetMePort;

@Configuration
public class CategoryConfigAdapter {

    @Bean
    public ListCategoryUseCasePort listCategoryUseCasePort(
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
    public CreateCategoryUseCasePort createCategoryUseCasePort(
        CategoryRepository categoryRepository,
        AttributeDefinitionRepository attributeDefinitionRepository,
        GetMePort getMePort
    ) {
        return new CreateCategoryUseCase(
                categoryRepository,
                attributeDefinitionRepository, 
                getMePort
            );
    }

    @Bean
    public DeleteCategoryUseCasePort deleteCategoryUseCasePort(
        CategoryRepository categoryRepository,
        GetMePort getMePort
    ) {
        return new DeleteCategoryUseCase(categoryRepository, getMePort);
    }

    @Bean
    public UpdateCategoryUseCasePort updateCategoryUseCasePort(
        CategoryRepository categoryRepository,
        CreateProductAttributeValuesInBulkForNewRequiredCategoryAttributesAsynchronously createProductAttributeValuesInBulkForNewRequiredCategoryAttributesAsynchronously,
        EventPublisher eventPublisher,
        GetMePort getMePort
    ) {
        return new UpdateCategoryUseCase(categoryRepository, createProductAttributeValuesInBulkForNewRequiredCategoryAttributesAsynchronously, eventPublisher, getMePort);
    }

    @Bean
    public CreateCategoryAttributeUseCasePort createCategoryAttributeUseCasePort(
        CategoryRepository categoryRepository,
        AttributeDefinitionRepository attributeDefinitionRepository,
        CreateProductAttributeValuesInBulkForNewRequiredCategoryAttributesAsynchronously createProductAttributeValuesInBulkForNewRequiredCategoryAttributesAsynchronously,
        EventPublisher eventPublisher,        
        GetMePort getMePort
    ) {
    
        return new CreateCategoryAttributeUseCase(
            categoryRepository, 
            attributeDefinitionRepository,
            createProductAttributeValuesInBulkForNewRequiredCategoryAttributesAsynchronously,
            eventPublisher,
            getMePort
        );
    }

    @Bean
    public DeleteCategoryAttributeUseCasePort deleteCategoryAttributeUseCasePort(
        CategoryRepository categoryRepository,
        EventPublisher eventPublisher,
        GetMePort getMePort
    ) {
        return new DeleteCategoryAttributeUseCase(
            categoryRepository,
            eventPublisher,
            getMePort
        );
    }
}
