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
class DeleteAttributeDefinitionUseCaseTest {

    @Mock
    private AttributeDefinitionRepository attributeDefinitionRepository;
    @Mock
    private GetMePort getMePort;

    @InjectMocks
    private DeleteAttributeDefinitionUseCase deleteUseCase;

    @Test
    void shouldDeleteAttributeDefinitionSuccessfully() {
        Me me = new Me(Id.generate(), Set.of(Permission.deleteAttributeDefinition()));
        when(getMePort.execute()).thenReturn(me);

        Slug findSlug = Slug.fromString("color");
        AttributeDefinition existing = new AttributeDefinition(Id.generate(), "Color", findSlug, DataType.STRING, false);
        when(attributeDefinitionRepository.getBySlug(findSlug)).thenReturn(existing);

        assertDoesNotThrow(() -> deleteUseCase.execute(findSlug));

        verify(attributeDefinitionRepository, times(1)).delete(existing);
    }

    @Test
    void shouldThrowExceptionWhenUserIsNull() {
        when(getMePort.execute()).thenReturn(null);

        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> deleteUseCase.execute(Slug.fromString("color"))
        );
        assertEquals("You do not have permission to perform this action", exception.getMessage());
        verifyNoInteractions(attributeDefinitionRepository);
    }
}
