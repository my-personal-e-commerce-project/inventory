package microservice.cloud.inventory.product.application.ports.in;

import microservice.cloud.inventory.product.application.dtos.ProductReadDTO;
import microservice.cloud.inventory.shared.application.dto.Pagination;

public interface ListProductsUseCasePort {

    public Pagination<ProductReadDTO> execute(int page, int limit);
}
