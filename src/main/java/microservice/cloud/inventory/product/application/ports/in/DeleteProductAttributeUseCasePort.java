package microservice.cloud.inventory.product.application.ports.in;

import microservice.cloud.inventory.shared.domain.value_objects.Id;

public interface DeleteProductAttributeUseCasePort {

    public void execute(Id productId, Id productAttributeId);
}
