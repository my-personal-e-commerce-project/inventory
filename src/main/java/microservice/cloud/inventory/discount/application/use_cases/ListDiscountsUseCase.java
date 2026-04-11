package microservice.cloud.inventory.discount.application.use_cases;

import microservice.cloud.inventory.discount.application.dtos.DiscountReadDTO;
import microservice.cloud.inventory.discount.application.ports.out.DiscountReadRepository;
import microservice.cloud.inventory.shared.application.dto.Pagination;

public class ListDiscountsUseCase {
    private final DiscountReadRepository discountReadRepository;

    public ListDiscountsUseCase(
        DiscountReadRepository discountReadRepository
    ) {
        this.discountReadRepository = discountReadRepository;
    }

    public Pagination<DiscountReadDTO> execute(int page, int size) {
        return discountReadRepository.listDiscounts(page, size);
    }
}
