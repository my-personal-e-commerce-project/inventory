package microservice.cloud.inventory.coupon.domain.entity;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

import jdk.jfr.Percentage;
import microservice.cloud.inventory.coupon.domain.value_objects.DiscountType;
import microservice.cloud.inventory.product.domain.value_objects.Price;
import microservice.cloud.inventory.shared.domain.value_objects.Id;

public class Coupon {
    private final Id id;
    private String name;
    private DiscountType discountType;
    private Percentage percentageValue;
    private Price decrementValue;
    private Set<String> allowedCategories;
    private boolean validAllCategories;
    private Price minPrice;
    private Price maxPrice;
    private boolean autoApply;
    private ZonedDateTime expiredAt;

    public Coupon(
        Id id,
        String name, 
        DiscountType discountType,
        Percentage percentageValue,
        Price decrementValue,
        Set<String> allowedCategories,
        boolean validAllCategories,
        Price minPrice,
        Price maxPrice,
        boolean autoApply,
        ZonedDateTime expiredAt
    ) {
        if(discountType.toString().equals(DiscountType.DECREMENT.toString()) && percentageValue != null)
            throw new RuntimeException("The percentageValue should be null");

        if(discountType.toString().equals(DiscountType.PERCENTAGE.toString()) && decrementValue != null)
            throw new RuntimeException("The decrementValue should be null");

        this.id = id;
        this.name = name;
        this.discountType = discountType;
        this.percentageValue = percentageValue;
        this.decrementValue = decrementValue;
        this.allowedCategories = allowedCategories;
        this.validAllCategories = validAllCategories;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.autoApply = autoApply;
        this.expiredAt = expiredAt;
    }

    public Id id() {return id;}
    public String name() {return name;}
    public DiscountType discountType() {return discountType;}
    public Percentage percentageValue() {return percentageValue;}
    public Price decrementValue() {return decrementValue;}
    public Set<String> allowedCategories() {return new HashSet<>(allowedCategories);}
    public boolean validAllCategories() {return validAllCategories;}
    public Price minPrice() {return minPrice;}
    public Price maxPrice() {return maxPrice;}
    public boolean autoApply() {return autoApply;}
    public ZonedDateTime expiredAt() {return expiredAt;}
}
