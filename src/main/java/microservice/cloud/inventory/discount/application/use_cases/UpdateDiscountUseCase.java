package microservice.cloud.inventory.discount.application.use_cases;

import microservice.cloud.inventory.discount.domain.entity.Discount;
import microservice.cloud.inventory.discount.domain.repository.DiscountRepository;
import microservice.cloud.inventory.shared.application.ports.out.GetMePort;
import microservice.cloud.inventory.shared.domain.value_objects.Me;
import microservice.cloud.inventory.shared.domain.value_objects.Permission;

public class UpdateDiscountUseCase {

    private final DiscountRepository discountRepository;
    private final GetMePort getMePort;

    public UpdateDiscountUseCase(
        DiscountRepository discountRepository,
        GetMePort getMePort
    ) {
        this.discountRepository = discountRepository;
        this.getMePort = getMePort;
    }

    public void execute(Discount discount) {
        Me me = getMePort.execute();

        if(me == null)
            throw new RuntimeException("You do not have permission to perform this action");

        me.IHavePermission(Permission.updateDiscount());

        discountRepository.update(discount);
    }
}
