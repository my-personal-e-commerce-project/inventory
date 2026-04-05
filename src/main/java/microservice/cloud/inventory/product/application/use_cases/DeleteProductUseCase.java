package microservice.cloud.inventory.product.application.use_cases;

import microservice.cloud.inventory.product.application.ports.in.DeleteProductUseCasePort;
import microservice.cloud.inventory.shared.application.ports.out.GetMePort;
import microservice.cloud.inventory.product.domain.entity.Product;
import microservice.cloud.inventory.product.domain.entity.ProductRepository;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;

public class DeleteProductUseCase implements DeleteProductUseCasePort {

    private ProductRepository productRepository;
    private GetMePort getMePort;

    public DeleteProductUseCase(
        ProductRepository productRepository,
        GetMePort getMePort
    ) {
        this.productRepository = productRepository;
        this.getMePort = getMePort;
    }

    @Override
    public void execute(Slug find_slug) {
        Product product = productRepository.findBySlug(find_slug);

        Product.canIDeleteThisProduct(getMePort.execute());

        productRepository.delete(product);
    }
}
