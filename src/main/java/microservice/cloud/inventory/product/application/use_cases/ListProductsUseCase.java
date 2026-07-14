package microservice.cloud.inventory.product.application.use_cases;

import microservice.cloud.inventory.product.application.dtos.ProductReadDTO;
import microservice.cloud.inventory.product.application.dtos.QueryProducts;
import microservice.cloud.inventory.product.application.ports.out.ProductReadRepository;
import microservice.cloud.inventory.shared.application.dto.Pagination;

public class ListProductsUseCase {

    private ProductReadRepository productReadRepository;

    public ListProductsUseCase(
        ProductReadRepository productReadRepository
    ) {
        this.productReadRepository = productReadRepository;
    }

    public Pagination<ProductReadDTO> execute(int page, int limit, QueryProducts query) {
        return productReadRepository.findAll(page, limit, query);
    }
}
