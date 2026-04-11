package microservice.cloud.inventory.discount.presentation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import microservice.cloud.inventory.discount.application.dtos.DiscountReadDTO;
import microservice.cloud.inventory.discount.application.use_cases.CreateDiscountUseCase;
import microservice.cloud.inventory.discount.application.use_cases.ListDiscountsUseCase;
import microservice.cloud.inventory.discount.domain.entity.Discount;
import microservice.cloud.inventory.discount.domain.value_objects.DiscountType;
import microservice.cloud.inventory.discount.domain.value_objects.Percentage;
import microservice.cloud.inventory.discount.presentation.validate.DiscountDTO;
import microservice.cloud.inventory.product.domain.value_objects.Price;
import microservice.cloud.inventory.product.domain.value_objects.Quantity;
import microservice.cloud.inventory.shared.application.dto.Pagination;
import microservice.cloud.inventory.shared.domain.value_objects.Id;

@RequiredArgsConstructor
@RequestMapping("/api/v1/discounts")
@RestController
public class DiscountController {

    private final CreateDiscountUseCase createDiscountUseCase;
    private final ListDiscountsUseCase listDiscountsUseCase;

    @GetMapping 
    public ResponseEntity<Pagination<DiscountReadDTO>> listDiscounts(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(
            listDiscountsUseCase.execute(page, size)
        );
    }

    @PostMapping
    public ResponseEntity<DiscountDTO> createDiscount(
        @RequestBody @Valid DiscountDTO discount
    ) {
        discount.setId(Id.generate().value());

        createDiscountUseCase.execute(toMap(discount));

        return ResponseEntity.ok(discount);
    }

    private Discount toMap(DiscountDTO discount) {

        return new Discount(
            Id.fromString(discount.getId()),
            discount.getName(),
            DiscountType.valueOf(discount.getDiscountType()),
            discount.getPercentageValue() == null
                ? null
                : new Percentage(discount.getPercentageValue()),
            discount.getDecrementValue(),
            discount.getAllowedCategories(),
            discount.isValidAllCategories(),
            discount.getMinPrice() == null
                ? null
                : new Price(discount.getMinPrice()),
            discount.getMaxPrice() == null
                ? null
                : new Price(discount.getMaxPrice()),
            new Quantity(discount.getMinStock()),
            new Quantity(discount.getMaxStock()),
            discount.isAutoApply(),
            discount.getExpiredAt()
        );
    }
}
