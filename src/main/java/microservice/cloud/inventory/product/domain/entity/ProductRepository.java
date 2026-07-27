package microservice.cloud.inventory.product.domain.entity;

import microservice.cloud.inventory.product.domain.value_objects.Quantity;
import microservice.cloud.inventory.shared.domain.value_objects.Id;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;

public interface ProductRepository {

    public void createProductAndStock(Product product, Id productStockId, Quantity stock);
    public void updateIfExists(Id id, Product product);
    public void delete(Product product);
    public Product findBySlug(Slug slug);
    public ProductAttributeValue findProductAttributeValueById(Id id);
}
