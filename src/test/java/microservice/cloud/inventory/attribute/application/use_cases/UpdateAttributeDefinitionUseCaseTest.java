package microservice.cloud.inventory.attribute.application.use_cases;

import java.util.Set;

import microservice.cloud.inventory.attribute.domain.entity.AttributeDefinition;
import microservice.cloud.inventory.attribute.domain.repository.AttributeDefinitionRepository;
import microservice.cloud.inventory.attribute.domain.value_objects.DataType;
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
class UpdateAttributeDefinitionUseCaseTest {

    @Mock
    private AttributeDefinitionRepository attributeDefinitionRepository;
    @Mock
    private GetMePort getMePort;

    @InjectMocks
    private UpdateAttributeDefinitionUseCase updateUseCase;

    @Test
    void shouldUpdateAttributeDefinitionSuccessfully() {
        Me me = new Me(Id.generate(), Set.of(Permission.updateAttributeDefinition()));
        when(getMePort.execute()).thenReturn(me);

        Slug findSlug = Slug.fromString("color");
        AttributeDefinition existing = new AttributeDefinition(Id.generate(), "Color", findSlug, DataType.STRING, false);
        when(attributeDefinitionRepository.getBySlug(findSlug)).thenReturn(existing);

        AttributeDefinition updated = updateUseCase.execute(findSlug, "New Color", Slug.fromString("new-color"), DataType.INTEGER, false);

        assertNotNull(updated);
        assertEquals("New Color", updated.name());
        assertEquals("new-color", updated.slug().value());
        assertEquals(DataType.INTEGER, updated.type());
        verify(attributeDefinitionRepository, times(1)).update(existing);
    }

    @Test
    void shouldThrowExceptionWhenUserIsNull() {
        when(getMePort.execute()).thenReturn(null);

        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> updateUseCase.execute(Slug.fromString("color"), "New Color", Slug.fromString("color"), DataType.STRING, false)
        );
        assertEquals("You do not have permission to perform this action", exception.getMessage());
        verifyNoInteractions(attributeDefinitionRepository);
    }
}
