package microservice.cloud.inventory.category.application.use_cases;

import java.util.List;

import microservice.cloud.inventory.category.application.dtos.CategoryReadDTO;
import microservice.cloud.inventory.category.application.dtos.QueryCategories;
import microservice.cloud.inventory.category.application.ports.out.CategoryReadRepository;
import microservice.cloud.inventory.shared.application.dto.Pagination;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListCategoryUseCaseTest {

    @Mock
    private CategoryReadRepository categoryReadRepository;

    @InjectMocks
    private ListCategoryUseCase listUseCase;

    @Test
    void shouldListCategoriesSuccessfully() {
        Pagination<CategoryReadDTO> expectedPagination = new Pagination<>(List.of(), 1, 1);
        when(categoryReadRepository.findAll(new QueryCategories(), 1, 10)).thenReturn(expectedPagination);

        Pagination<CategoryReadDTO> result = listUseCase.execute(new QueryCategories(), 1, 10);

        assertNotNull(result);
        assertEquals(expectedPagination, result);
        verify(categoryReadRepository, times(1)).findAll(new QueryCategories(), 1, 10);
    }
}
