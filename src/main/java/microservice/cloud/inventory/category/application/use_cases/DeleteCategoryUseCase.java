package microservice.cloud.inventory.category.application.use_cases;

import microservice.cloud.inventory.category.application.ports.in.DeleteCategoryUseCasePort;
import microservice.cloud.inventory.category.domain.entity.Category;
import microservice.cloud.inventory.category.domain.repository.CategoryRepository;
import microservice.cloud.inventory.shared.application.ports.in.GetMePort;
import microservice.cloud.inventory.shared.application.ports.out.EventPublishedPort;
import microservice.cloud.inventory.shared.domain.value_objects.Id;

public class DeleteCategoryUseCase implements DeleteCategoryUseCasePort {

    private CategoryRepository categoryRepository;
    private GetMePort getMePort;
    private EventPublishedPort eventPublishedPort;

    public DeleteCategoryUseCase(
        CategoryRepository categoryRepository,
        EventPublishedPort eventPublishedPort,
        GetMePort getMePort
    ) {
        this.categoryRepository = categoryRepository;
        this.eventPublishedPort = eventPublishedPort;
        this.getMePort = getMePort;
    }

    @Override
    public void execute(Id id) {
        Category category = categoryRepository.findById(id);

        category.delete(getMePort.execute());

        categoryRepository.delete(category);

        eventPublishedPort.publish(category.events());
    }
}
