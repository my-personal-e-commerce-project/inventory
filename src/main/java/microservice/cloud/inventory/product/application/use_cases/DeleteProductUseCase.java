package microservice.cloud.inventory.product.application.use_cases;

import microservice.cloud.inventory.product.application.ports.in.DeleteProductUseCasePort;
import microservice.cloud.inventory.shared.application.ports.in.GetMePort;
import microservice.cloud.inventory.product.domain.entity.Product;
import microservice.cloud.inventory.product.domain.entity.ProductRepository;
import microservice.cloud.inventory.shared.domain.value_objects.Id;

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
    public void execute(Id id) {
        Product product = productRepository.findById(id);

        product.delete(getMePort.execute());

        productRepository.delete(product);
    }
}
