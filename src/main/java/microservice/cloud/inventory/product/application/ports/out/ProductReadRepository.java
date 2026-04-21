package microservice.cloud.inventory.product.application.ports.put;

import microservice.cloud.inventory.product.application.dtos.ProductReadDTO;
import microservice.cloud.inventory.shared.application.dto.Pagination;

public interface ProductReadRepository {

    public Pagination<ProductReadDTO> findAll(int page, int limit);
}
