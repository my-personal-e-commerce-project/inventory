package microservice.cloud.inventory.product.application.ports.in;

import microservice.cloud.inventory.product.domain.entity.Product;

public interface CreateProductUseCasePort {

    public void execute(Product product);
}
