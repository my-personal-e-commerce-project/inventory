package microservice.cloud.inventory.product.application.use_cases;

import microservice.cloud.inventory.product.application.ports.in.DeleteProductAttributeUseCasePort;
import microservice.cloud.inventory.product.domain.entity.Product;
import microservice.cloud.inventory.product.domain.entity.ProductRepository;
import microservice.cloud.inventory.shared.domain.value_objects.Id;

public class DeleteProductAttributeUseCase implements DeleteProductAttributeUseCasePort {

    private ProductRepository productRepository;

    public DeleteProductAttributeUseCase(
        ProductRepository productRepository
    ) {
        this.productRepository = productRepository;
    }

    @Override
    public void execute(Id productId, Id productAttributeId) {
        Product product = productRepository.findById(productId);

        product.removeAttribute(productAttributeId);

        productRepository.removeProductAttribute(productId, productAttributeId);
    } 
}
