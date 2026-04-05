package microservice.cloud.inventory.coupon.domain.repository;

import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import microservice.cloud.inventory.coupon.domain.entity.Coupon;
import microservice.cloud.inventory.product.domain.entity.Product;

public interface CouponRepository {

    public void save(Coupon coupon);
    public void update(Coupon coupon);
    public void delete(Id id);
    public void applyAutomaticCoupons(Product product);
}
