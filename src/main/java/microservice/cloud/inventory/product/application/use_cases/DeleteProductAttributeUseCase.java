package microservice.cloud.inventory.product.application.use_cases;

import microservice.cloud.inventory.category.domain.repository.CategoryRepository;
import microservice.cloud.inventory.shared.application.ports.out.GetMePort;
import microservice.cloud.inventory.product.domain.entity.Product;
import microservice.cloud.inventory.product.domain.entity.ProductAttributeValue;
import microservice.cloud.inventory.product.domain.entity.ProductRepository;
import microservice.cloud.inventory.shared.domain.value_objects.Id;
import microservice.cloud.inventory.shared.domain.value_objects.Me;
import microservice.cloud.inventory.shared.domain.value_objects.Permission;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;

public class DeleteProductAttributeUseCase {

    private ProductRepository productRepository;
    private CategoryRepository categoryRepository;
    private GetMePort getMePort;

    public DeleteProductAttributeUseCase(
        ProductRepository productRepository,
        CategoryRepository categoryRepository,
        GetMePort getMePort
    ) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.getMePort = getMePort;
    }

    public Product execute(Slug find_slug, Id productAttributeId) {
        Me me = getMePort.execute();

        if(me == null)
            throw new RuntimeException("You do not have permission to perform this action");

        me.IHavePermission(Permission.updateProduct());

        Product product = productRepository.findBySlug(find_slug);

        productRepository.updateIfExists(product.id(), (p) -> {
            ProductAttributeValue productAttributeValue = productRepository 
                .findProductAttributeValueById(productAttributeId);

            product.removeProductAttribute(
                productAttributeId, 
                categoryRepository.getCategoryAttributeByAttributeDefinitionId(
                    productAttributeValue.attribute_definition_id()
                )
            );
        });

        return product;
    } 
}
