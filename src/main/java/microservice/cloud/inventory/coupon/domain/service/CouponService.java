package microservice.cloud.inventory.coupon.domain.service;

import java.time.ZonedDateTime;
import java.util.Set;

import microservice.cloud.inventory.coupon.domain.entity.Coupon;
import microservice.cloud.inventory.coupon.domain.value_objects.DiscountType;
import microservice.cloud.inventory.coupon.domain.value_objects.Percentage;
import microservice.cloud.inventory.product.domain.value_objects.Price;
import microservice.cloud.inventory.product.domain.value_objects.Quantity;
import microservice.cloud.inventory.shared.domain.value_objects.Id;
import microservice.cloud.inventory.shared.domain.value_objects.Me;
import microservice.cloud.inventory.shared.domain.value_objects.Permission;

public class CouponService {

    public Coupon createCouponAndValidatePermissions(
        Id id,
        Me me,
        String name, 
        DiscountType discountType,
        Percentage percentageValue,
        Price decrementValue,
        Set<String> allowedCategories,
        boolean validAllCategories,
        Price minPrice,
        Price maxPrice,
        Quantity minStock,
        Quantity maxStock,
        boolean autoApply,
        ZonedDateTime expiredAt
    ) {
        if(me == null)
            throw new RuntimeException("You do not have permission to perform this action");

        me.IHavePermission(Permission.createProduct());

        return new Coupon(
            id,
            name, 
            discountType,
            percentageValue,
            decrementValue,
            allowedCategories,
            validAllCategories,
            minPrice,
            maxPrice,
            minStock,
            maxStock,
            autoApply,
            expiredAt
        ) ;
    }
}
