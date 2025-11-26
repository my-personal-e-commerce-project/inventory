package microservice.cloud.inventory.product.application.use_cases;

import microservice.cloud.inventory.product.application.dtos.ProductReadDTO;
import microservice.cloud.inventory.product.application.ports.in.ListProductsUseCasePort;
import microservice.cloud.inventory.product.application.ports.put.ProductReadRepository;
import microservice.cloud.inventory.shared.application.dto.Pagination;

public class ListProductsUseCase implements ListProductsUseCasePort {

    private ProductReadRepository productReadRepository;

    public ListProductsUseCase(
        ProductReadRepository productReadRepository
    ) {
        this.productReadRepository = productReadRepository;
    }

    @Override
    public Pagination<ProductReadDTO> execute(int page, int limit) {
        return productReadRepository.findAll(page, limit);
    }
}
