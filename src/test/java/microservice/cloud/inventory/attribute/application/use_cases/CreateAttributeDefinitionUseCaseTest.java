package microservice.cloud.inventory.attribute.application.use_cases;

import java.util.List;
import java.util.Set;

import microservice.cloud.inventory.attribute.domain.entity.AttributeDefinition;
import microservice.cloud.inventory.attribute.domain.repository.AttributeDefinitionRepository;
import microservice.cloud.inventory.attribute.domain.value_objects.DataType;
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
class CreateAttributeDefinitionUseCaseTest {

    @Mock
    private AttributeDefinitionRepository attributeDefinitionRepository;
    @Mock
    private EventPublisher eventPublisher;
    @Mock
    private GetMePort getMePort;

    @InjectMocks
    private CreateAttributeDefinitionUseCase createUseCase;

    @Test
    void shouldCreateAttributeDefinitionSuccessfully() {
        Me me = new Me(Id.generate(), Set.of(Permission.createAttributeDefinition()));
        when(getMePort.execute()).thenReturn(me);

        Id id = Id.generate();
        String name = "Test Attr";
        Slug slug = Slug.fromString("test-attr");
        DataType type = DataType.STRING;

        assertDoesNotThrow(() -> createUseCase.execute(id, name, slug, type, true));

        verify(attributeDefinitionRepository, times(1)).save(any(AttributeDefinition.class));
        verify(eventPublisher, times(1)).publish(anyList());
    }

    @Test
    void shouldThrowExceptionWhenUserIsNull() {
        when(getMePort.execute()).thenReturn(null);

        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> createUseCase.execute(Id.generate(), "Test Attr", Slug.fromString("test-attr"), DataType.STRING, true)
        );
        assertEquals("You do not have permission to perform this action", exception.getMessage());
        verifyNoInteractions(attributeDefinitionRepository, eventPublisher);
    }
}
