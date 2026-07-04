package microservice.cloud.inventory.category.application.use_cases;

import java.util.Map;
import java.util.Set;

import microservice.cloud.inventory.attribute.domain.entity.AttributeDefinition;
import microservice.cloud.inventory.attribute.domain.repository.AttributeDefinitionRepository;
import microservice.cloud.inventory.attribute.domain.value_objects.DataType;
import microservice.cloud.inventory.category.domain.entity.Category;
import microservice.cloud.inventory.category.domain.entity.CategoryAttribute;
import microservice.cloud.inventory.category.domain.repository.CategoryRepository;
import microservice.cloud.inventory.category.domain.value_objects.Status;
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
class CreateCategoryUseCaseTest {

    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private AttributeDefinitionRepository attributeDefinitionRepository;
    @Mock
    private GetMePort getMePort;

    @InjectMocks
    private CreateCategoryUseCase createUseCase;

    @Test
    void shouldCreateCategorySuccessfully() {
        Me me = new Me(Id.generate(), Set.of(Permission.createCategory()));
        when(getMePort.execute()).thenReturn(me);

        Id defId = Id.generate();
        CategoryAttribute attr = new CategoryAttribute(Id.generate(), defId, true, true, true);
        Category category = new Category(
            Id.generate(), "Electronics", Slug.fromString("electronics"), null, Status.ENABLED, Set.of(attr)
        );

        AttributeDefinition def = new AttributeDefinition(defId, "Brand", Slug.fromString("brand"), DataType.STRING, false);
        when(attributeDefinitionRepository.findByIds(Set.of(defId.value()))).thenReturn(Map.of(defId.value(), def));

        assertDoesNotThrow(() -> createUseCase.execute(category));

        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    void shouldThrowExceptionWhenUserIsNull() {
        when(getMePort.execute()).thenReturn(null);
        Category category = mock(Category.class);

        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> createUseCase.execute(category)
        );
        assertEquals("You do not have permission to perform this action.", exception.getMessage());
        verifyNoInteractions(categoryRepository, attributeDefinitionRepository);
    }
}
