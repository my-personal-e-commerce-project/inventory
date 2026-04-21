package microservice.cloud.inventory.discount.domain.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import microservice.cloud.inventory.discount.domain.event.CreatedDiscount;
import microservice.cloud.inventory.discount.domain.value_objects.DiscountType;
import microservice.cloud.inventory.discount.domain.value_objects.Percentage;
import microservice.cloud.inventory.product.domain.value_objects.Price;
import microservice.cloud.inventory.product.domain.value_objects.Quantity;
import microservice.cloud.inventory.shared.domain.entity.AggregateRoot;
import microservice.cloud.inventory.shared.domain.value_objects.Id;

public class Discount extends AggregateRoot {
    private final Id id;
    private String name;
    private DiscountType discountType;
    private Percentage percentageValue;
    private Price decrementValue;
    private Set<String> allowedCategories;
    private boolean globalCategories;
    private Price minPrice;
    private Price maxPrice;
    private Quantity minStock;
    private Quantity maxStock;
    private boolean autoApply;
    private boolean isACoupon;
    private LocalDateTime expiredAt;

    public Discount(
        Id id,
        String name, 
        DiscountType discountType,
        Percentage percentageValue,
        Price decrementValue,
        Set<String> allowedCategories,
        boolean globalCategories,
        Price minPrice,
        Price maxPrice,
        Quantity minStock,
        Quantity maxStock,
        boolean autoApply,
        boolean isACoupon,
        LocalDateTime expiredAt
    ) {
        if(isACoupon && autoApply)
            throw new RuntimeException("A coupon can not be applied automatically.");

        if(globalCategories && !allowedCategories.isEmpty())
            throw new RuntimeException("This discounts cannot have categories field, because globalCategories field is false");

        if(!globalCategories && allowedCategories.isEmpty())
            throw new RuntimeException("This discounts should have categories field, because globalCategories field is true");

        if(!isACoupon && DiscountType.DECREMENT.equals(discountType))
            throw new RuntimeException("Only coupons can be of the decrement type.");

        if(discountType == null)
            throw new RuntimeException("The discountType field cannot be null.");

        if(discountType.toString().equals(DiscountType.DECREMENT.toString()) && percentageValue != null)
            throw new RuntimeException("The percentageValue should be null.");

        if(discountType.toString().equals(DiscountType.PERCENTAGE.toString()) && decrementValue != null)
            throw new RuntimeException("The decrementValue should be null.");

        this.id = id;
        this.name = name;
        this.discountType = discountType;
        this.percentageValue = percentageValue;
        this.decrementValue = decrementValue;
        this.allowedCategories = allowedCategories;
        this.globalCategories = globalCategories;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.minStock = minStock;
        this.maxStock = maxStock;
        this.autoApply = autoApply;
        this.isACoupon = isACoupon;
        this.expiredAt = expiredAt;
    }

    public static Discount factory(
        Id id,
        String name, 
        DiscountType discountType,
        Percentage percentageValue,
        Price decrementValue,
        Set<String> allowedCategories,
        boolean globalCategories,
        Price minPrice,
        Price maxPrice,
        Quantity minStock,
        Quantity maxStock,
        boolean autoApply,
        boolean isACoupon,
        LocalDateTime expiredAt
    ) {
        Discount discount = new Discount(
            id, 
            name, 
            discountType, 
            percentageValue, 
            decrementValue, 
            allowedCategories, 
            globalCategories, 
            minPrice, 
            maxPrice, 
            minStock, 
            maxStock, 
            autoApply,
            isACoupon,
            expiredAt
        );

        discount.publishEvent(
            new CreatedDiscount(
                id.value(),
                name,
                discountType.toString(),
                percentageValue == null? null: percentageValue.value(),
                decrementValue == null? null: decrementValue.value(),
                allowedCategories,
                globalCategories,
                minPrice == null? null: minPrice.value(),
                maxPrice == null? null: maxPrice.value(),
                minStock == null? null: minStock.value(),
                maxStock == null? null: maxStock.value(),
                autoApply, 
                expiredAt
            )
        );

        return discount;
    }

    public Id id() {return id;}
    public String name() {return name;}
    public DiscountType discountType() {return discountType;}
    public Percentage percentageValue() {return percentageValue;}
    public Price decrementValue() {return decrementValue;}
    public Set<String> allowedCategories() {
        return allowedCategories != null
            ? new HashSet<>(allowedCategories)
            : null;
    }
    public boolean globalCategories() {return globalCategories;}
    public Price minPrice() {return minPrice;}
    public Price maxPrice() {return maxPrice;}
    public Quantity minStock() {return minStock;}
    public Quantity maxStock() {return maxStock;}
    public boolean autoApply() {return autoApply;}
    public boolean isACoupon() {return isACoupon;}
    public LocalDateTime expiredAt() {return expiredAt;}
}
