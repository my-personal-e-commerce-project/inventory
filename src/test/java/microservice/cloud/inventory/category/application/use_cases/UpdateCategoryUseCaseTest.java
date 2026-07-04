package microservice.cloud.inventory.category.application.use_cases;

import java.util.Set;

import microservice.cloud.inventory.category.domain.entity.Category;
import microservice.cloud.inventory.category.domain.entity.CategoryAttribute;
import microservice.cloud.inventory.category.domain.repository.CategoryRepository;
import microservice.cloud.inventory.category.domain.value_objects.Status;
import microservice.cloud.inventory.shared.application.ports.out.EventPublisher;
import microservice.cloud.inventory.shared.application.ports.out.GetMePort;
import microservice.cloud.inventory.shared.domain.exception.DataNotFound;
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
class UpdateCategoryUseCaseTest {

    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private EventPublisher eventPublisher;
    @Mock
    private GetMePort getMePort;

    @InjectMocks
    private UpdateCategoryUseCase updateUseCase;

    @Test
    void shouldUpdateCategorySuccessfully() {
        Me me = new Me(Id.generate(), Set.of(Permission.updateCategory()));
        when(getMePort.execute()).thenReturn(me);

        Slug findSlug = Slug.fromString("electronics");
        CategoryAttribute attr = new CategoryAttribute(Id.generate(), Id.generate(), true, true, true);
        Category category = new Category(
            Id.generate(), "Electronics", findSlug, null, Status.ENABLED, Set.of(attr)
        );
        when(categoryRepository.findBySlug(findSlug)).thenReturn(category);

        assertDoesNotThrow(() -> updateUseCase.execute(findSlug, "New Electronics", Slug.fromString("new-electronics"), null, Set.of(attr)));

        assertEquals("New Electronics", category.name());
        verify(categoryRepository, times(1)).update(category);
        verify(eventPublisher, times(1)).publish(anyList());
    }

    @Test
    void shouldThrowExceptionWhenCategoryIsDisabled() {
        Me me = new Me(Id.generate(), Set.of(Permission.updateCategory()));
        when(getMePort.execute()).thenReturn(me);

        Slug findSlug = Slug.fromString("electronics");
        Category category = new Category(
            Id.generate(), "Electronics", findSlug, null, Status.DISABLED, Set.of()
        );
        when(categoryRepository.findBySlug(findSlug)).thenReturn(category);

        assertThrows(
            DataNotFound.class,
            () -> updateUseCase.execute(findSlug, "New Electronics", Slug.fromString("new-electronics"), null, Set.of())
        );
        verify(categoryRepository, never()).update(any());
    }
}
