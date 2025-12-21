package microservice.cloud.inventory.category.application.use_cases;

import java.util.List;

import microservice.cloud.inventory.category.application.ports.in.CreateCategoryUseCasePort;
import microservice.cloud.inventory.category.domain.entity.Category;
import microservice.cloud.inventory.category.domain.entity.CategoryAttribute;
import microservice.cloud.inventory.category.domain.repository.CategoryRepository;
import microservice.cloud.inventory.shared.application.ports.in.GetMePort;
import microservice.cloud.inventory.shared.domain.value_objects.Id;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;

public class CreateCategoryUseCase implements CreateCategoryUseCasePort {

    private CategoryRepository categoryRepository;
    private GetMePort getMePort;

    public CreateCategoryUseCase(
        CategoryRepository categoryRepository,
        GetMePort getMePort
    ) {
        this.categoryRepository = categoryRepository;
        this.getMePort = getMePort;
    }

    @Override
    public Category execute(
        Id id,
        String name, 
        Slug slug, 
        Id parent_id, 
        List<CategoryAttribute> categoryAttributes
    ) {
        Category category = Category.create(getMePort.execute(), id, name, slug, parent_id, categoryAttributes);

        categoryRepository.save(category);
        
        return category;
    }
}
