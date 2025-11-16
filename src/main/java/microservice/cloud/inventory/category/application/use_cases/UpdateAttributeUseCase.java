package microservice.cloud.inventory.category.application.use_cases;

import microservice.cloud.inventory.category.application.ports.in.UpdateAttributeUseCasePort;
import microservice.cloud.inventory.category.domain.entity.CategoryAttribute;
import microservice.cloud.inventory.category.domain.repository.CategoryAttributeRepository;

public class UpdateAttributeUseCase implements UpdateAttributeUseCasePort {

    private CategoryAttributeRepository categoryAttributeRepository;

    public UpdateAttributeUseCase(
        CategoryAttributeRepository categoryAttributeRepository
    ) {
        this.categoryAttributeRepository = categoryAttributeRepository;
    }

    @Override
    public void execute(CategoryAttribute categoryAttribute) {
        categoryAttributeRepository.update(categoryAttribute);
    }
}
