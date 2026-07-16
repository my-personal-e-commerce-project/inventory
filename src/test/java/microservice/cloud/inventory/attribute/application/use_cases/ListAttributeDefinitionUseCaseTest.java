package microservice.cloud.inventory.attribute.application.use_cases;

import java.util.List;

import microservice.cloud.inventory.attribute.application.ports.dto.AttributeDefinitionReadDTO;
import microservice.cloud.inventory.attribute.application.ports.dto.QueryAttributeDefinitions;
import microservice.cloud.inventory.attribute.application.ports.out.AttributeDefinitionReadRepository;
import microservice.cloud.inventory.shared.application.dto.Pagination;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListAttributeDefinitionUseCaseTest {

    @Mock
    private AttributeDefinitionReadRepository attributeDefinitionReadRepository;

    @InjectMocks
    private ListAttributeDefinitionUseCase listUseCase;

    @Test
    void shouldListAttributeDefinitionsSuccessfully() {
        Pagination<AttributeDefinitionReadDTO> expectedPagination = new Pagination<>(List.of(), 1, 1);
        when(attributeDefinitionReadRepository.findAll(new QueryAttributeDefinitions(), 1, 10)).thenReturn(expectedPagination);

        Pagination<AttributeDefinitionReadDTO> result = listUseCase.execute(new QueryAttributeDefinitions(), 1, 10);

        assertNotNull(result);
        assertEquals(expectedPagination, result);
        verify(attributeDefinitionReadRepository, times(1)).findAll(new QueryAttributeDefinitions(), 1, 10);
    }
}
