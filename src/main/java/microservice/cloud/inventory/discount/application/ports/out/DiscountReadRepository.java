package microservice.cloud.inventory.discount.application.ports.out;

import microservice.cloud.inventory.discount.application.dtos.DiscountReadDTO;
import microservice.cloud.inventory.shared.application.dto.Pagination;

public interface DiscountReadRepository {

    public Pagination<DiscountReadDTO> listDiscounts(int page, int size);
}
