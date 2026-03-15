package microservice.cloud.inventory.product.application.ports.in;

import microservice.cloud.inventory.product.domain.entity.Product;
import microservice.cloud.inventory.shared.domain.value_objects.Id;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;

public interface DeleteProductAttributeUseCasePort {

    public Product execute(Slug find_slug, Id productAttributeId);
}
