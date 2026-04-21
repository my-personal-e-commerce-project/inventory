package microservice.cloud.inventory.product.infrastructure.adapters;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import microservice.cloud.inventory.discount.domain.repository.DiscountRepository;
import microservice.cloud.inventory.product.application.ports.out.ApplyDiscountsToThisProductAsynchronously;
import microservice.cloud.inventory.product.domain.entity.Product;

@RequiredArgsConstructor
@Component
public class ApplyDiscountsToThisProductAsynchronouslyImpl implements ApplyDiscountsToThisProductAsynchronously {

    private final DiscountRepository discountRepository;
  
    @Async
    @Override
    public void execute(Product product) {
        discountRepository.applyDiscountsToThisProduct(product);
    }
}
