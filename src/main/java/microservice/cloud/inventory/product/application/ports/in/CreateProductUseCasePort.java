package microservice.cloud.inventory.product.application.ports.in;

import java.util.Set;

import microservice.cloud.inventory.product.domain.entity.Product;
import microservice.cloud.inventory.product.domain.entity.ProductAttributeValue;
import microservice.cloud.inventory.product.domain.value_objects.Price;
import microservice.cloud.inventory.product.domain.value_objects.Quantity;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;

public interface CreateProductUseCasePort {

    public Product execute(
        String title,
        Slug slug,
        String description,
        Set<String> categories, 
        Price price, 
        Set<ProductAttributeValue> attributeValues,
        Quantity stock,
        Set<String> images,
        Set<String> tags    
    );
}
