package microservice.cloud.inventory.discount.application.use_cases;

import java.time.LocalDateTime;
import java.util.Set;

import microservice.cloud.inventory.discount.application.ports.out.PublisherOfAutomaticallyAppliedDiscountsCreated;
import microservice.cloud.inventory.discount.domain.entity.Discount;
import microservice.cloud.inventory.discount.domain.repository.DiscountRepository;
import microservice.cloud.inventory.discount.domain.value_objects.DiscountType;
import microservice.cloud.inventory.discount.domain.value_objects.Percentage;
import microservice.cloud.inventory.product.domain.value_objects.Price;
import microservice.cloud.inventory.product.domain.value_objects.Quantity;
import microservice.cloud.inventory.shared.application.ports.out.GetMePort;
import microservice.cloud.inventory.shared.domain.value_objects.Id;
import microservice.cloud.inventory.shared.domain.value_objects.Me;
import microservice.cloud.inventory.shared.domain.value_objects.Permission;

public class CreateDiscountUseCase {
    private final DiscountRepository discountRepository;
    private final PublisherOfAutomaticallyAppliedDiscountsCreated publisherOfAutomaticallyAppliedDiscountsCreated;
    private final GetMePort getMePort;

    public CreateDiscountUseCase (
        DiscountRepository discountRepository,
        PublisherOfAutomaticallyAppliedDiscountsCreated publisherOfAutomaticallyAppliedDiscountsCreated,
        GetMePort getMePort
    ) {
        this.discountRepository = discountRepository;
        this.publisherOfAutomaticallyAppliedDiscountsCreated = publisherOfAutomaticallyAppliedDiscountsCreated;
        this.getMePort = getMePort;
    }

    public void execute(
        Id id,
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
        LocalDateTime expiredAt
    ) {
        Me me = getMePort.execute();

        if(me == null)
            throw new RuntimeException("You do not have permission to perform this action");

        me.IHavePermission(Permission.createDiscount());

        Discount discount = Discount.factory(
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
            false,
            expiredAt
        );

        discountRepository.save(discount);

        if(autoApply)
            publisherOfAutomaticallyAppliedDiscountsCreated.publish(discount);
    }
}
