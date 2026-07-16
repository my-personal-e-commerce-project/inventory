package microservice.cloud.inventory.category.infrastructure.persistence.repository;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import microservice.cloud.inventory.category.application.dtos.CategoryReadDTO;
import microservice.cloud.inventory.category.application.dtos.QueryCategories;
import microservice.cloud.inventory.shared.application.dto.Pagination;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryReadRepositoryJdbcAdapterTest {

    @Mock
    private JdbcTemplate jdbcTemplate;
    @Mock
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @InjectMocks
    private CategoryReadRepositoryJdbcAdapter readRepositoryAdapter;

    @Test
    void shouldGetCategoriesByIdsSuccessfully() {
        Set<String> ids = Set.of("cat-123");
        List<CategoryReadDTO> expectedList = List.of(
            new CategoryReadDTO("cat-123", "Electronics", "electronics", null, List.of())
        );

        when(namedParameterJdbcTemplate.query(
            anyString(),
            any(MapSqlParameterSource.class),
            any(CategoryReadResultSetExtractor.class)
        )).thenReturn(expectedList);

        List<CategoryReadDTO> result = readRepositoryAdapter.getCategoriesByIds(ids);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("cat-123", result.get(0).getId());
    }

    @Test
    void shouldFindAllWithPaginationSuccessfully() {
        List<CategoryReadDTO> expectedList = List.of(
            new CategoryReadDTO("cat-123", "Electronics", "electronics", null, List.of())
        );

        when(namedParameterJdbcTemplate.query(
            anyString(),
            any(MapSqlParameterSource.class),
            any(CategoryReadResultSetExtractor.class)
        )).thenReturn(expectedList);

        when(jdbcTemplate.queryForObject(anyString(), eq(Long.class)))
            .thenReturn(1L);

        Pagination<CategoryReadDTO> result = readRepositoryAdapter.findAll(new QueryCategories(null), 0, 10);

        assertNotNull(result);
        assertEquals(expectedList, result.results());
        assertEquals(0, result.current_page());
        assertEquals(0, result.last_page());
    }
}
