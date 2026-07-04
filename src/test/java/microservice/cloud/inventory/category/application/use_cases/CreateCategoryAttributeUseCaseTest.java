package microservice.cloud.inventory.category.application.use_cases;

import java.util.Set;

import microservice.cloud.inventory.attribute.domain.entity.AttributeDefinition;
import microservice.cloud.inventory.attribute.domain.repository.AttributeDefinitionRepository;
import microservice.cloud.inventory.attribute.domain.value_objects.DataType;
import microservice.cloud.inventory.category.domain.entity.Category;
import microservice.cloud.inventory.category.domain.entity.CategoryAttribute;
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
class CreateCategoryAttributeUseCaseTest {

    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private AttributeDefinitionRepository attributeDefinitionRepository;
    @Mock
    private EventPublisher eventPublisher;
    @Mock
    private GetMePort getMePort;

    @InjectMocks
    private CreateCategoryAttributeUseCase createAttrUseCase;

    @Test
    void shouldCreateCategoryAttributeSuccessfully() {
        Me me = new Me(Id.generate(), Set.of(Permission.updateCategory()));
        when(getMePort.execute()).thenReturn(me);

        Slug findSlug = Slug.fromString("electronics");
        Category category = new Category(
            Id.generate(), "Electronics", findSlug, null, Status.ENABLED, Set.of()
        );
        when(categoryRepository.findBySlug(findSlug)).thenReturn(category);

        Id defId = Id.generate();
        CategoryAttribute categoryAttribute = new CategoryAttribute(Id.generate(), defId, true, true, true);
        AttributeDefinition def = new AttributeDefinition(defId, "Brand", Slug.fromString("brand"), DataType.STRING, false);
        when(attributeDefinitionRepository.getById(defId)).thenReturn(def);

        assertDoesNotThrow(() -> createAttrUseCase.execute(findSlug, categoryAttribute));

        assertEquals(1, category.categoryAttributes().size());
        verify(categoryRepository, times(1)).update(category);
        verify(eventPublisher, times(1)).publish(anyList());
    }

    @Test
    void shouldThrowExceptionWhenUserIsNull() {
        when(getMePort.execute()).thenReturn(null);
        CategoryAttribute attr = mock(CategoryAttribute.class);

        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> createAttrUseCase.execute(Slug.fromString("electronics"), attr)
        );
        assertEquals("You do not have permission to perform this action.", exception.getMessage());
        verifyNoInteractions(categoryRepository, attributeDefinitionRepository, eventPublisher);
    }
}
