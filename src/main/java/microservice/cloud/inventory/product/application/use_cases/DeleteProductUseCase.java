package microservice.cloud.inventory.product.application.use_cases;

import microservice.cloud.inventory.product.application.ports.in.DeleteProductUseCasePort;
import microservice.cloud.inventory.product.domain.entity.Product;
import microservice.cloud.inventory.product.domain.entity.ProductRepository;
import microservice.cloud.inventory.shared.application.ports.out.GetMePort;
import microservice.cloud.inventory.shared.domain.value_objects.Me;
import microservice.cloud.inventory.shared.domain.value_objects.Permission;
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
        Me me = getMePort.execute();
        
        if(me == null)
            throw new RuntimeException("You do not have permission to perform this action");

        me.IHavePermission(Permission.deleteProduct());

        Product product = productRepository.findBySlug(find_slug);
        
        productRepository.delete(product);
    }
}
