package microservice.cloud.inventory.category.infrastructure.adapter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import microservice.cloud.inventory.category.application.ports.in.CreateCategoryUseCasePort;
import microservice.cloud.inventory.category.application.ports.in.DeleteCategoryUseCasePort;
import microservice.cloud.inventory.category.application.ports.in.ListCategoryUseCasePort;
import microservice.cloud.inventory.category.application.ports.in.UpdateCategoryUseCasePort;
import microservice.cloud.inventory.category.application.ports.out.CategoryReadRepository;

import microservice.cloud.inventory.category.application.use_cases.CreateCategoryUseCase;
import microservice.cloud.inventory.category.application.use_cases.DeleteCategoryUseCase;
import microservice.cloud.inventory.category.application.use_cases.ListCategoryUseCase;
import microservice.cloud.inventory.category.application.use_cases.UpdateCategoryUseCase;
import microservice.cloud.inventory.category.domain.repository.CategoryRepository;

@Configuration
public class ConfigAdapter {

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
}
