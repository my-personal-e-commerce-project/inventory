package microservice.cloud.inventory.category.application.use_cases;

import java.util.List;
import java.util.Set;

import microservice.cloud.inventory.category.application.dtos.CategoryReadDTO;
import microservice.cloud.inventory.category.application.ports.out.CategoryReadRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListCategoriesByIdsUseCaseTest {

    @Mock
    private CategoryReadRepository categoryReadRepository;

    @InjectMocks
    private ListCategoriesByIdsUseCase listUseCase;

    @Test
    void shouldListCategoriesByIdsSuccessfully() {
        Set<String> ids = Set.of("cat-1", "cat-2");
        List<CategoryReadDTO> expectedList = List.of();
        when(categoryReadRepository.getCategoriesByIds(ids)).thenReturn(expectedList);

        List<CategoryReadDTO> result = listUseCase.execute(ids);

        assertNotNull(result);
        assertEquals(expectedList, result);
        verify(categoryReadRepository, times(1)).getCategoriesByIds(ids);
    }
}
