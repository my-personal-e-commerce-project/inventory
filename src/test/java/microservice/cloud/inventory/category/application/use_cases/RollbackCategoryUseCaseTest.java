package microservice.cloud.inventory.category.application.use_cases;

import java.util.Set;

import microservice.cloud.inventory.category.domain.entity.Category;
import microservice.cloud.inventory.category.domain.repository.CategoryRepository;
import microservice.cloud.inventory.category.domain.value_objects.Status;
import microservice.cloud.inventory.shared.domain.value_objects.Id;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RollbackCategoryUseCaseTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private RollbackCategoryUseCase rollbackUseCase;

    @Test
    void shouldRollbackCategorySuccessfully() {
        Id id = Id.generate();
        Category category = new Category(
            id, "Electronics", Slug.fromString("electronics"), null, Status.DISABLED, Set.of()
        );
        when(categoryRepository.findById(id)).thenReturn(category);

        assertDoesNotThrow(() -> rollbackUseCase.execute(id));

        assertEquals(Status.ENABLED, category.status());
        verify(categoryRepository, times(1)).update(category);
    }
}
