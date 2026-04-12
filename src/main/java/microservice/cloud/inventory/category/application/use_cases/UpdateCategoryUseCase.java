package microservice.cloud.inventory.category.application.use_cases;

import java.util.Set;

import microservice.cloud.inventory.category.application.ports.in.UpdateCategoryUseCasePort;
import microservice.cloud.inventory.category.domain.entity.Category;
import microservice.cloud.inventory.category.domain.entity.CategoryAttribute;
import microservice.cloud.inventory.category.domain.repository.CategoryRepository;
import microservice.cloud.inventory.product.domain.entity.ProductRepository;
import microservice.cloud.inventory.shared.application.ports.out.GetMePort;
import microservice.cloud.inventory.shared.domain.value_objects.Id;
import microservice.cloud.inventory.shared.domain.value_objects.Me;
import microservice.cloud.inventory.shared.domain.value_objects.Permission;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;

public class UpdateCategoryUseCase implements UpdateCategoryUseCasePort {

    private CategoryRepository categoryRepository;
    private ProductRepository productRepository;
    private GetMePort getMePort;

    public UpdateCategoryUseCase(
        CategoryRepository categoryRepository,
        ProductRepository productRepository,
        GetMePort getMePort
    ) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
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

        category.categoryAttributes().forEach(attr -> {
            // TODO: cambiar esto a enviar todos los eventos a un publisher
            if(attr.is_required())
                productRepository.massCreateProductAttributeValuesByCategory(category.id(), attr.attribute_definition());
        });
    }
}
