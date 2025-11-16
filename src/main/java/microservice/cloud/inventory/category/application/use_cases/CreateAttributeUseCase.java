package microservice.cloud.inventory.category.application.use_cases;

import microservice.cloud.inventory.category.application.ports.in.CreateAttributeUseCasePort;
import microservice.cloud.inventory.category.domain.entity.CategoryAttribute;
import microservice.cloud.inventory.category.domain.repository.CategoryAttributeRepository;

public class CreateAttributeUseCase implements CreateAttributeUseCasePort {

    private CategoryAttributeRepository categoryAttributeRepository;

    public CreateAttributeUseCase(
        CategoryAttributeRepository categoryAttributeRepository
    ) {
        this.categoryAttributeRepository = categoryAttributeRepository;
    }

    @Override
    public void execute(CategoryAttribute categoryAttribute) {
        categoryAttributeRepository.save(categoryAttribute);
    }
}
