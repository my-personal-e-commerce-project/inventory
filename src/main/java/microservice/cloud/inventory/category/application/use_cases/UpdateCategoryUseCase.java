package microservice.cloud.inventory.category.application.use_cases;

import java.util.List;

import microservice.cloud.inventory.category.application.ports.in.UpdateCategoryUseCasePort;
import microservice.cloud.inventory.category.domain.entity.Category;
import microservice.cloud.inventory.category.domain.entity.CategoryAttribute;
import microservice.cloud.inventory.category.domain.repository.CategoryRepository;
import microservice.cloud.inventory.shared.application.ports.in.GetMePort;
import microservice.cloud.inventory.shared.application.ports.out.EventPublishedPort;
import microservice.cloud.inventory.shared.domain.value_objects.Id;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;

public class UpdateCategoryUseCase implements UpdateCategoryUseCasePort {

    private CategoryRepository categoryRepository;
    private GetMePort getMePort;
    private EventPublishedPort eventPublishedPort;

    public UpdateCategoryUseCase(
        CategoryRepository categoryRepository,
        EventPublishedPort eventPublishedPort,
        GetMePort getMePort
    ) {
        this.categoryRepository = categoryRepository;
        this.eventPublishedPort = eventPublishedPort;
        this.getMePort = getMePort;
    }

    @Override
    public void execute(Id id, String name, Slug slug, Id parent_id, List<CategoryAttribute> categoryAttributes) {
        Category categoryDB = categoryRepository.findById(id);

        categoryDB.update(getMePort.execute(), name, slug, parent_id, categoryAttributes);

        categoryRepository.update(categoryDB);

        eventPublishedPort.publish(categoryDB.events());
    }
}
