package microservice.cloud.inventory.product.application.ports.in;

import microservice.cloud.inventory.shared.domain.value_objects.Slug;

public interface DeleteProductUseCasePort {

    public void execute(Slug find_slug);
}
