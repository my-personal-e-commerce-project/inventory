package microservice.cloud.inventory.product.application.use_cases;

import java.util.ArrayList;
import java.util.List;

import microservice.cloud.inventory.category.domain.entity.CategoryAttribute;
import microservice.cloud.inventory.category.domain.repository.CategoryRepository;
import microservice.cloud.inventory.product.application.ports.in.DeleteProductAttributeUseCasePort;
import microservice.cloud.inventory.shared.application.ports.in.GetMePort;
import microservice.cloud.inventory.product.domain.entity.Product;
import microservice.cloud.inventory.product.domain.entity.ProductRepository;
import microservice.cloud.inventory.shared.domain.value_objects.Id;

public class DeleteProductAttributeUseCase implements DeleteProductAttributeUseCasePort {

    private ProductRepository productRepository;
    private GetMePort getMePort;
    private CategoryRepository categoryRepository;

    public DeleteProductAttributeUseCase(
        ProductRepository productRepository,
        CategoryRepository categoryRepository,
        GetMePort getMePort
    ) {
        this.productRepository = productRepository;
        this.getMePort = getMePort;
    }

    @Override
    public Product execute(Id productId, Id productAttributeId) {
        Product product = productRepository.findById(productId);

        product.removeAttribute(getMePort.execute(), productAttributeId);

        List<CategoryAttribute> attrs = 
           categoryRepository 
            .getCategoryAttributesByCategoryIds(
                product.categories()
            );

        product.validAttributes(new ArrayList<>(attrs));

        productRepository.update(product);

        return product;
    } 
}
