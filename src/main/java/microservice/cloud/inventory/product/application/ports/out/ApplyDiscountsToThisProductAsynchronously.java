package microservice.cloud.inventory.product.application.ports.out;

import microservice.cloud.inventory.product.domain.entity.Product;

public interface ApplyDiscountsToThisProductAsynchronously {

    public void execute(Product product);
}
