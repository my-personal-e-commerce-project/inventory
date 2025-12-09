package microservice.cloud.inventory.category.application.use_cases;

import java.util.List;

import microservice.cloud.inventory.category.application.ports.in.CreateCategoryUseCasePort;
import microservice.cloud.inventory.category.domain.entity.Category;
import microservice.cloud.inventory.category.domain.entity.CategoryAttribute;
import microservice.cloud.inventory.category.domain.repository.CategoryRepository;
import microservice.cloud.inventory.shared.application.ports.in.GetMePort;
import microservice.cloud.inventory.shared.application.ports.out.EventPublishedPort;
import microservice.cloud.inventory.shared.domain.value_objects.Id;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;

public class CreateCategoryUseCase implements CreateCategoryUseCasePort {

    private CategoryRepository categoryRepository;
    private EventPublishedPort eventPublishedPort;
    private GetMePort getMePort;

    public CreateCategoryUseCase(
        CategoryRepository categoryRepository,
        EventPublishedPort eventPublishedPort,
        GetMePort getMePort
    ) {
        this.categoryRepository = categoryRepository;
        this.eventPublishedPort = eventPublishedPort;
        this.getMePort = getMePort;
    }

    @Override
    public void execute(String name, Slug slug, Id parent_id, List<CategoryAttribute> categoryAttributes) {
        Category category = Category.factory(getMePort.execute(), name, slug, parent_id, categoryAttributes);

        categoryRepository.save(category);
        
        eventPublishedPort.publish(category.events());
    }
}
