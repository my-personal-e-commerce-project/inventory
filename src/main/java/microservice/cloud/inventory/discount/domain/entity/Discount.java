package microservice.cloud.inventory.discount.domain.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import microservice.cloud.inventory.discount.domain.value_objects.DiscountType;
import microservice.cloud.inventory.discount.domain.value_objects.Percentage;
import microservice.cloud.inventory.product.domain.value_objects.Price;
import microservice.cloud.inventory.product.domain.value_objects.Quantity;
import microservice.cloud.inventory.shared.domain.value_objects.Id;

public class Discount {
    private final Id id;
    private String name;
    private DiscountType discountType;
    private Percentage percentageValue;
    private Double decrementValue;
    private Set<String> allowedCategories;
    private boolean validAllCategories;
    private Price minPrice;
    private Price maxPrice;
    private Quantity minStock;
    private Quantity maxStock;
    private boolean autoApply;
    private LocalDateTime expiredAt;

    public Discount(
        Id id,
        String name, 
        DiscountType discountType,
        Percentage percentageValue,
        Double decrementValue,
        Set<String> allowedCategories,
        boolean validAllCategories,
        Price minPrice,
        Price maxPrice,
        Quantity minStock,
        Quantity maxStock,
        boolean autoApply,
        LocalDateTime expiredAt
    ) {
        if(discountType == null)
            throw new RuntimeException("The discountType field cannot be null");

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
        this.minStock = minStock;
        this.maxStock = maxStock;
        this.autoApply = autoApply;
        this.expiredAt = expiredAt;
    }

    public Id id() {return id;}
    public String name() {return name;}
    public DiscountType discountType() {return discountType;}
    public Percentage percentageValue() {return percentageValue;}
    public Double decrementValue() {return decrementValue;}
    public Set<String> allowedCategories() {
        return allowedCategories != null
            ? new HashSet<>(allowedCategories)
            : null;
    }
    public boolean validAllCategories() {return validAllCategories;}
    public Price minPrice() {return minPrice;}
    public Price maxPrice() {return maxPrice;}
    public Quantity minStock() {return minStock;}
    public Quantity maxStock() {return maxStock;}
    public boolean autoApply() {return autoApply;}
    public LocalDateTime expiredAt() {return expiredAt;}
}
