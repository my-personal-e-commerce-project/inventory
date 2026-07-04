package microservice.cloud.inventory.category.application.use_cases;

import java.util.Set;

import microservice.cloud.inventory.category.domain.entity.Category;
import microservice.cloud.inventory.category.domain.repository.CategoryRepository;
import microservice.cloud.inventory.category.domain.value_objects.Status;
import microservice.cloud.inventory.shared.application.ports.out.EventPublisher;
import microservice.cloud.inventory.shared.application.ports.out.GetMePort;
import microservice.cloud.inventory.shared.domain.value_objects.Id;
import microservice.cloud.inventory.shared.domain.value_objects.Me;
import microservice.cloud.inventory.shared.domain.value_objects.Permission;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteCategoryUseCaseTest {

    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private EventPublisher eventPublisher;
    @Mock
    private GetMePort getMePort;

    @InjectMocks
    private DeleteCategoryUseCase deleteUseCase;

    @Test
    void shouldDeleteCategorySuccessfully() {
        Me me = new Me(Id.generate(), Set.of(Permission.deleteCategory()));
        when(getMePort.execute()).thenReturn(me);

        Slug findSlug = Slug.fromString("electronics");
        Category category = new Category(
            Id.generate(), "Electronics", findSlug, null, Status.ENABLED, Set.of()
        );
        when(categoryRepository.findBySlug(findSlug)).thenReturn(category);

        assertDoesNotThrow(() -> deleteUseCase.execute(findSlug));

        assertEquals(Status.DISABLED, category.status());
        verify(categoryRepository, times(1)).update(category);
        verify(eventPublisher, times(1)).publish(anyList());
    }

    @Test
    void shouldThrowExceptionWhenUserIsNull() {
        when(getMePort.execute()).thenReturn(null);

        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> deleteUseCase.execute(Slug.fromString("electronics"))
        );
        assertEquals("You do not have permission to perform this action.", exception.getMessage());
        verifyNoInteractions(categoryRepository, eventPublisher);
    }
}
