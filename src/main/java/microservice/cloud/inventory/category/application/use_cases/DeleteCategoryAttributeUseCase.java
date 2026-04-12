package microservice.cloud.inventory.category.application.use_cases;

import microservice.cloud.inventory.category.application.ports.in.DeleteCategoryAttributeUseCasePort;
import microservice.cloud.inventory.category.domain.entity.Category;
import microservice.cloud.inventory.category.domain.entity.CategoryAttribute;
import microservice.cloud.inventory.category.domain.repository.CategoryRepository;
import microservice.cloud.inventory.product.domain.entity.ProductRepository;
import microservice.cloud.inventory.shared.application.ports.out.GetMePort;
import microservice.cloud.inventory.shared.domain.value_objects.Id;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;

public class DeleteCategoryAttributeUseCase implements DeleteCategoryAttributeUseCasePort {

    private CategoryRepository categoryRepository;
    private ProductRepository productRepository;
    private GetMePort getMePort;

    public DeleteCategoryAttributeUseCase(
        CategoryRepository categoryRepository,
        ProductRepository productRepository,
        GetMePort getMePort
    ) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.getMePort = getMePort;
    }
    
    public Category execute(Slug find_slug, Id categoryAttributeId) {
        Category category = categoryRepository.findBySlug(find_slug);

        CategoryAttribute catAttr = categoryRepository.getCategoryAttributeById(categoryAttributeId);

        category.removeCategoryAttribute(getMePort.execute(), categoryAttributeId);

        categoryRepository.update(category);

        // TODO: cambiar esto a enviar todos los eventos a un publisher
        productRepository.deleteOrphanAttributeValues(category.id(), catAttr.attribute_definition_id());

        return category;
    }
}
