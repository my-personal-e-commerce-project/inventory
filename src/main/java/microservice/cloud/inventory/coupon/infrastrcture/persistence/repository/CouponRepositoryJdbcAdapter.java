package microservice.cloud.inventory.coupon.infrastrcture.persistence.repository;

import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import lombok.RequiredArgsConstructor;
import microservice.cloud.inventory.coupon.domain.entity.Coupon;
import microservice.cloud.inventory.coupon.domain.repository.CouponRepository;
import microservice.cloud.inventory.product.domain.entity.Product;

@RequiredArgsConstructor
@Repository
public class CouponRepositoryJdbcAdapter implements CouponRepository {

    @Override
    public void save(Coupon coupon) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void update(Coupon coupon) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void delete(Id id) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void applyAutomaticCoupons(Product product) {
        // TODO Auto-generated method stub
        
    }
}
