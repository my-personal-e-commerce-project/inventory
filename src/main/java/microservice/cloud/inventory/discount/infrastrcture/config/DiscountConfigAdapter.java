package microservice.cloud.inventory.discount.infrastrcture.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import microservice.cloud.inventory.discount.application.ports.out.DiscountReadRepository;
import microservice.cloud.inventory.discount.application.use_cases.CreateDiscountUseCase;
import microservice.cloud.inventory.discount.application.use_cases.DeleteDiscountUseCase;
import microservice.cloud.inventory.discount.application.use_cases.ListDiscountsUseCase;
import microservice.cloud.inventory.discount.domain.repository.DiscountRepository;
import microservice.cloud.inventory.shared.application.ports.out.GetMePort;

@Configuration
public class DiscountConfigAdapter {
   
    @Bean
    public CreateDiscountUseCase createCouponUseCase(
        DiscountRepository discountRepository,
        GetMePort getMePort
    ) {
        return new CreateDiscountUseCase(discountRepository, getMePort);
    }

    @Bean
    public DeleteDiscountUseCase deleteDiscountUseCase(
        DiscountRepository discountRepository,
        GetMePort getMePort
    ) {
        return new DeleteDiscountUseCase(discountRepository, getMePort);
    }

    @Bean 
    public ListDiscountsUseCase listCouponsUseCase(
        DiscountReadRepository discountReadRepository
    ) {
        return new ListDiscountsUseCase(discountReadRepository);
    }
}
