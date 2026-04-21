package microservice.cloud.inventory.discount.infrastrcture.adapters;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import microservice.cloud.inventory.discount.application.ports.out.PublisherOfAutomaticallyAppliedDiscountsCreated;
import microservice.cloud.inventory.discount.domain.entity.Discount;
import microservice.cloud.inventory.product.domain.entity.ProductRepository;

@RequiredArgsConstructor
@Component
public class PublisherOfAutomaticallyAppliedDiscountsCreatedImpl implements PublisherOfAutomaticallyAppliedDiscountsCreated {

    private final ProductRepository productRepository;

    @Async
    @Override
    public void publish(Discount discount) {
        productRepository.applyThisAutomaticDiscountToTheCorrespondingProducts(discount);
    }
}
