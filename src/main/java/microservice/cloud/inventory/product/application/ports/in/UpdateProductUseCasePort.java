package microservice.cloud.inventory.product.application.ports.in;

import java.util.Set;

import microservice.cloud.inventory.product.domain.entity.Product;
import microservice.cloud.inventory.product.domain.entity.ProductAttributeValue;
import microservice.cloud.inventory.product.domain.value_objects.Price;
import microservice.cloud.inventory.product.domain.value_objects.Quantity;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;

public interface UpdateProductUseCasePort {
 
    public Product execute(
        Slug find_slug,
        String title, 
        Slug slug, 
        String description,
        Set<String> categories,
        Price price,
        Quantity stock,
        Set<String> images,
        Set<ProductAttributeValue> attributes,
        Set<String> tags
    );
}
