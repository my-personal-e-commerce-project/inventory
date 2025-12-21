package microservice.cloud.inventory.product.application.ports.in;

import java.util.List;

import microservice.cloud.inventory.product.domain.entity.Product;
import microservice.cloud.inventory.product.domain.entity.ProductAttributeValue;
import microservice.cloud.inventory.product.domain.value_objects.Price;
import microservice.cloud.inventory.product.domain.value_objects.Quantity;
import microservice.cloud.inventory.shared.domain.value_objects.Id;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;

public interface CreateProductUseCasePort {

    public Product execute(
        Id id,
        String title, 
        Slug slug, 
        String description,
        List<String> categories,
        Price price,
        Quantity stock,
        List<String> images,
        List<ProductAttributeValue> attributes,
        List<String> tags
    );
}
