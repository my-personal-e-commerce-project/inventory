package microservice.cloud.inventory.category.application.use_cases;

import microservice.cloud.inventory.category.application.ports.in.DeleteCategoryUseCasePort;
import microservice.cloud.inventory.category.domain.entity.Category;
import microservice.cloud.inventory.category.domain.repository.CategoryRepository;
import microservice.cloud.inventory.shared.application.ports.out.GetMePort;
import microservice.cloud.inventory.shared.domain.value_objects.Id;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;

public class DeleteCategoryUseCase implements DeleteCategoryUseCasePort {

    private CategoryRepository categoryRepository;
    private GetMePort getMePort;

    public DeleteCategoryUseCase(
        CategoryRepository categoryRepository,
        GetMePort getMePort
    ) {
        this.categoryRepository = categoryRepository;
        this.getMePort = getMePort;
    }

    @Override
    public void execute(Slug find_slug) {
        Category category = categoryRepository.findBySlug(find_slug);

        category.canIDeleteThisCategory(getMePort.execute());

        categoryRepository.delete(category);
    }
}
