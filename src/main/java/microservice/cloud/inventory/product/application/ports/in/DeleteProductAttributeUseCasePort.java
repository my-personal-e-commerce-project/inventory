package microservice.cloud.inventory.product.application.ports.in;

import microservice.cloud.inventory.product.domain.entity.Product;
import microservice.cloud.inventory.shared.domain.value_objects.Id;

public interface DeleteProductAttributeUseCasePort {

    public Product execute(Id productId, Id productAttributeId);
}
