package microservice.cloud.inventory.product.application.use_cases;

import java.util.List;

import microservice.cloud.inventory.product.application.dtos.ProductReadDTO;
import microservice.cloud.inventory.product.application.dtos.QueryProducts;
import microservice.cloud.inventory.product.application.ports.out.ProductReadRepository;
import microservice.cloud.inventory.shared.application.dto.Pagination;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListProductsUseCaseTest {

    @Mock
    private ProductReadRepository productReadRepository;

    @InjectMocks
    private ListProductsUseCase listProductsUseCase;

    @Test
    void shouldListProductsSuccessfully() {
        // GIVEN
        int page = 1;
        int limit = 10;
        QueryProducts query = new QueryProducts("search", List.of("cat1"), 0.0, 100.0, 0, 50, true);
        
        ProductReadDTO productDto = new ProductReadDTO(
            "product-id", "Product Title", "product-slug", "Description", 
            List.of("cat1"), true, List.of(), 19.99, 10, 5, List.of(), List.of()
        );
        Pagination<ProductReadDTO> expectedPagination = new Pagination<>(List.of(productDto), 1, 1);
        
        when(productReadRepository.findAll(page, limit, query)).thenReturn(expectedPagination);

        // WHEN
        Pagination<ProductReadDTO> result = listProductsUseCase.execute(page, limit, query);

        // THEN
        assertNotNull(result);
        assertEquals(expectedPagination, result);
        verify(productReadRepository, times(1)).findAll(page, limit, query);
    }
}
