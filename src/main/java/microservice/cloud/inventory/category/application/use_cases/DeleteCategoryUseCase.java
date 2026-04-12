package microservice.cloud.inventory.category.application.use_cases;

import microservice.cloud.inventory.category.application.ports.in.DeleteCategoryUseCasePort;
import microservice.cloud.inventory.category.domain.entity.Category;
import microservice.cloud.inventory.category.domain.repository.CategoryRepository;
import microservice.cloud.inventory.product.domain.entity.ProductRepository;
import microservice.cloud.inventory.shared.application.ports.out.GetMePort;
import microservice.cloud.inventory.shared.domain.value_objects.Me;
import microservice.cloud.inventory.shared.domain.value_objects.Permission;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;

public class DeleteCategoryUseCase implements DeleteCategoryUseCasePort {

    private CategoryRepository categoryRepository;
    private ProductRepository productRepository;
    private GetMePort getMePort;

    public DeleteCategoryUseCase(
        CategoryRepository categoryRepository,
        ProductRepository productRepository,
        GetMePort getMePort
    ) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.getMePort = getMePort;
    }

    @Override
    public void execute(Slug find_slug) {
        Me me = getMePort.execute();

        if(me == null)
            throw new RuntimeException("You do not have permission to perform this action.");

        me.IHavePermission(Permission.deleteCategory());

        Category category = categoryRepository.findBySlug(find_slug);

        categoryRepository.delete(category);

        category.categoryAttributes().forEach(a -> {
            // TODO: cambiar esto a enviar todos los eventos a un publisher
            productRepository.deleteOrphanAttributeValues(category.id(), a.attribute_definition_id());
        });
    }
}
