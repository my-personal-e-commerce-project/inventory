package microservice.cloud.inventory.product.application.ports.in;

import microservice.cloud.inventory.shared.domain.value_objects.Id;

public interface DeleteProductUseCasePort {

    public void execute(Id id);
}
