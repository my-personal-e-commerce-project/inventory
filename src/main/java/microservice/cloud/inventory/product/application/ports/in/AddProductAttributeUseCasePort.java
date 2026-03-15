package microservice.cloud.inventory.product.application.ports.in;

import microservice.cloud.inventory.product.domain.entity.Product;
import microservice.cloud.inventory.product.domain.entity.ProductAttributeValue;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;

public interface AddProductAttributeUseCasePort {

    public Product execute(Slug find_slug, ProductAttributeValue productAttributeValue);
}
