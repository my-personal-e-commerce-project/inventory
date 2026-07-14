package microservice.cloud.inventory.product.application.ports.out;

import microservice.cloud.inventory.product.application.dtos.ProductReadDTO;
import microservice.cloud.inventory.product.application.dtos.QueryProducts;
import microservice.cloud.inventory.shared.application.dto.Pagination;

public interface ProductReadRepository {

    public Pagination<ProductReadDTO> findAll(int page, int limit, QueryProducts query);
}
