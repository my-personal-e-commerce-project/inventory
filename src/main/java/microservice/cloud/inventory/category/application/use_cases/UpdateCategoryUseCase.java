package microservice.cloud.inventory.category.application.use_cases;

import java.util.Set;

import microservice.cloud.inventory.category.application.ports.in.UpdateCategoryUseCasePort;
import microservice.cloud.inventory.category.domain.entity.Category;
import microservice.cloud.inventory.category.domain.entity.CategoryAttribute;
import microservice.cloud.inventory.category.domain.repository.CategoryRepository;
import microservice.cloud.inventory.shared.application.ports.out.GetMePort;
import microservice.cloud.inventory.shared.domain.value_objects.Id;
import microservice.cloud.inventory.shared.domain.value_objects.Me;
import microservice.cloud.inventory.shared.domain.value_objects.Permission;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;

public class UpdateCategoryUseCase implements UpdateCategoryUseCasePort {

    private CategoryRepository categoryRepository;
    private GetMePort getMePort;

    public UpdateCategoryUseCase(
        CategoryRepository categoryRepository,
        GetMePort getMePort
    ) {
        this.categoryRepository = categoryRepository;
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

        category.update(getMePort.execute(), name, slug, parent_id, categoryAttributes);

        categoryRepository.update(category);
    }
}
