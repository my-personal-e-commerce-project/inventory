package microservice.cloud.inventory.product.application.use_cases;

import microservice.cloud.inventory.category.domain.repository.CategoryRepository;
import microservice.cloud.inventory.product.application.ports.in.DeleteProductAttributeUseCasePort;
import microservice.cloud.inventory.shared.application.ports.out.GetMePort;
import microservice.cloud.inventory.product.domain.entity.Product;
import microservice.cloud.inventory.product.domain.entity.ProductAttributeValue;
import microservice.cloud.inventory.product.domain.entity.ProductRepository;
import microservice.cloud.inventory.shared.domain.value_objects.Id;

public class DeleteProductAttributeUseCase implements DeleteProductAttributeUseCasePort {

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

    @Override
    public Product execute(Id productId, Id productAttributeId) {
        Product product = productRepository.findById(productId);

        ProductAttributeValue productAttributeValue = productRepository 
            .findProductAttributeValueById(productAttributeId);

        product.removeAttribute(
            getMePort.execute(), 
            productAttributeId, 
            categoryRepository.getCategoryAttributeByAttributeDefinitionId(
                productAttributeValue.id()
            )
        );

        productRepository.update(product);

        return product;
    } 
}
