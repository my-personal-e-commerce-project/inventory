package microservice.cloud.inventory.discount.domain.repository;

import java.util.List;
import java.util.Set;

import microservice.cloud.inventory.discount.domain.entity.Discount;
import microservice.cloud.inventory.product.domain.entity.Product;
import microservice.cloud.inventory.shared.domain.value_objects.Id;

public interface DiscountRepository {

    public void save(Discount coupon);
    public void update(Discount coupon);
    public void delete(Discount discount);
    public Discount getById(Id id);
    public List<Discount> getDiscountsByIds(Set<String> couponIds);
    public void applyDiscountsToThisProduct(Product product);
}
