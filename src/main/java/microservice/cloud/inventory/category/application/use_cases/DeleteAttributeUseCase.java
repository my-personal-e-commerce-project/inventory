package microservice.cloud.inventory.category.application.use_cases;

import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import microservice.cloud.inventory.category.application.ports.in.DeleteAttributeUseCasePort;
import microservice.cloud.inventory.category.domain.repository.CategoryAttributeRepository;

public class DeleteAttributeUseCase implements DeleteAttributeUseCasePort {

    private CategoryAttributeRepository categoryAttributeRepository;

    public DeleteAttributeUseCase(CategoryAttributeRepository categoryAttributeRepository) {
        this.categoryAttributeRepository = categoryAttributeRepository;
    }

    @Override
    public void execute(Id id) {
        categoryAttributeRepository.delete(id);    
    }
}
