package microservice.cloud.inventory.category.application.use_cases;

import java.util.List;
import java.util.Set;

import microservice.cloud.inventory.category.application.ports.in.UpdateCategoryUseCasePort;
import microservice.cloud.inventory.category.domain.entity.Category;
import microservice.cloud.inventory.category.domain.entity.CategoryAttribute;
import microservice.cloud.inventory.category.domain.repository.CategoryRepository;
import microservice.cloud.inventory.category.domain.value_objects.Status;
import microservice.cloud.inventory.shared.application.ports.out.EventPublisher;
import microservice.cloud.inventory.shared.application.ports.out.GetMePort;
import microservice.cloud.inventory.shared.domain.exception.DataNotFound;
import microservice.cloud.inventory.shared.domain.value_objects.Id;
import microservice.cloud.inventory.shared.domain.value_objects.Me;
import microservice.cloud.inventory.shared.domain.value_objects.Permission;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;

public class UpdateCategoryUseCase implements UpdateCategoryUseCasePort {

    private CategoryRepository categoryRepository;
    private EventPublisher eventPublisher;
    private GetMePort getMePort;

    public UpdateCategoryUseCase(
        CategoryRepository categoryRepository,
        EventPublisher eventPublisher,
        GetMePort getMePort
    ) {
        this.categoryRepository = categoryRepository;
        this.eventPublisher = eventPublisher;
        this.getMePort = getMePort;
    }

    @Override
    public void execute(
        Slug find_slug, 
        String name, 
        Slug slug, 
        Id parent_id, 
        Set<CategoryAttribute> categoryAttributes
    ) {
        Me me = getMePort.execute();

        if(me == null)
            throw new RuntimeException(
                "You do not have permission to perform this action."
                );

        me.IHavePermission(Permission.updateCategory());

        Category category = categoryRepository.findBySlug(find_slug);

        if(category.status() == Status.DISABLED)
            throw new DataNotFound("Category not found");

        category.update(name, slug, parent_id, categoryAttributes);

        categoryRepository.update(category);

        eventPublisher.publish(category.getEvents());
    }
}
