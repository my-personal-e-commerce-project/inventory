package microservice.cloud.inventory.product.application.use_cases;

import microservice.cloud.inventory.product.application.ports.in.AddProductAttributeUseCasePort;
import microservice.cloud.inventory.product.domain.entity.Product;
import microservice.cloud.inventory.product.domain.entity.ProductAttributeValue;
import microservice.cloud.inventory.product.domain.entity.ProductRepository;
import microservice.cloud.inventory.shared.domain.value_objects.Id;

public class AddProductAttributeUseCase implements AddProductAttributeUseCasePort {

    private ProductRepository productRepository;

    public AddProductAttributeUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void execute(Id productId, ProductAttributeValue productAttributeValue) {
        Product product = productRepository.findById(productId);

        product.addProductAttribute(productAttributeValue);

        productRepository.addProductAttribute(productId, productAttributeValue);
    } 
}
