package microservice.cloud.inventory.category.application.use_cases;

import microservice.cloud.inventory.category.application.ports.in.DeleteCategoryUseCasePort;
import microservice.cloud.inventory.category.domain.entity.Category;
import microservice.cloud.inventory.category.domain.repository.CategoryRepository;
import microservice.cloud.inventory.shared.application.ports.out.EventPublisher;
import microservice.cloud.inventory.shared.application.ports.out.GetMePort;
import microservice.cloud.inventory.shared.domain.value_objects.Me;
import microservice.cloud.inventory.shared.domain.value_objects.Permission;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;

public class DeleteCategoryUseCase implements DeleteCategoryUseCasePort {

    private CategoryRepository categoryRepository;
    private EventPublisher eventPublisher;
    private GetMePort getMePort;

    public DeleteCategoryUseCase(
        CategoryRepository categoryRepository,
        EventPublisher eventPublisher,
        GetMePort getMePort
    ) {
        this.categoryRepository = categoryRepository;
        this.eventPublisher = eventPublisher;
        this.getMePort = getMePort;
    }

    @Override
    public void execute(Slug find_slug) {
        Me me = getMePort.execute();

        if(me == null)
            throw new RuntimeException("You do not have permission to perform this action.");

        me.IHavePermission(Permission.deleteCategory());

        Category category = categoryRepository.findBySlug(find_slug);

        category = categoryRepository.updateIfExists(category.id(), (c) -> {
            c.deleteCategory();
        });
      
        if(category.getEvents() != null && !category.getEvents().isEmpty()) {
            eventPublisher.publish(category.getEvents());
        };
    }
}
