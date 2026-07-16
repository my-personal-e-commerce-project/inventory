package microservice.cloud.inventory.attribute.infrastructure.persistence.repository;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import microservice.cloud.inventory.attribute.application.ports.dto.AttributeDefinitionReadDTO;
import microservice.cloud.inventory.attribute.application.ports.dto.QueryAttributeDefinitions;
import microservice.cloud.inventory.attribute.infrastructure.persistence.model.AttributeDefinitionEntity;
import microservice.cloud.inventory.shared.application.dto.Pagination;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttributeDefinitionReadRepositoryJdbcAdapapterTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private AttributeDefinitionJdbcRepository attributeDefinitionJdbcRepository;

    @InjectMocks
    private AttributeDefinitionReadRepositoryJdbcAdapapter readRepositoryAdapter;

    @Test
    void shouldFindAllAttributeDefinitionsWithPaginationSuccessfully() {
        // GIVEN
        QueryAttributeDefinitions query = new QueryAttributeDefinitions("search-query");
        AttributeDefinitionEntity entity = new AttributeDefinitionEntity("id-123", "Name", "slug", "STRING", false);
        
        when(attributeDefinitionJdbcRepository.findAllAndSearch("search-query", 10, 0))
            .thenReturn(List.of(entity));
        
        when(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM products;", Long.class))
            .thenReturn(1L);

        // WHEN
        Pagination<AttributeDefinitionReadDTO> result = readRepositoryAdapter.findAll(query, 0, 10);

        // THEN
        assertNotNull(result);
        assertEquals(1, result.results().size());
        assertEquals(0, result.current_page());
        assertEquals(0, result.last_page());
        
        AttributeDefinitionReadDTO item = result.results().get(0);
        assertEquals("id-123", item.id());
        assertEquals("Name", item.name());
        assertEquals("slug", item.slug());
        assertEquals("STRING", item.type());
        assertFalse(item.is_global());
    }
}
