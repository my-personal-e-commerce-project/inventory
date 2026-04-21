package microservice.cloud.inventory.discount.application.ports.out;

import microservice.cloud.inventory.discount.domain.entity.Discount;

public interface PublisherOfAutomaticallyAppliedDiscountsCreated {

    public void publish(Discount discount);
}
