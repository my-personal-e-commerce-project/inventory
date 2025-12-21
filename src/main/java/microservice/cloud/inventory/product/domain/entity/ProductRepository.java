package microservice.cloud.inventory.product.domain.entity;

import microservice.cloud.inventory.shared.domain.value_objects.Id;

public interface ProductRepository {

    public void save(Product product);
    public void update(Product product);
    public void delete(Product product);
    public Product findById(Id id);
}
