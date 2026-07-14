package microservice.cloud.inventory.product.infrastructure.persistence.repository;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import microservice.cloud.inventory.product.application.dtos.ProductReadDTO;
import microservice.cloud.inventory.product.application.dtos.QueryProducts;
import microservice.cloud.inventory.shared.application.dto.Pagination;

@ExtendWith(MockitoExtension.class)
public class ProductReadRepositoryJdbcAdapterTest {

    @Mock
    private JdbcTemplate jdbcTemplate;
    @Mock
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @InjectMocks
    private ProductReadRepositoryJdbcAdapter productReadRepositoryJdbcAdapter;

    @Test
    void shouldGetAllProductsSuccessfullyWhenThereIsOneProductWithCurrentPageOfOneValueAndLastPageOfOneValue() {
        // GIVEN
        List<ProductReadDTO> expectedList = List.of(
            new ProductReadDTO("prod-123", "Electronics", "electronics", "generic description", List.of(), true, List.of(), 0.22, 12, null, null)
        );

        when(namedParameterJdbcTemplate.query(
            Mockito.anyString(),
            Mockito.any(MapSqlParameterSource.class),
            Mockito.any(ProductResultSetExtractor.class)
        )).thenReturn(expectedList);

        when(jdbcTemplate.queryForObject(
            Mockito.anyString(),
            Long.class 
        )).thenReturn(1L);


        // WHEN
        Pagination<ProductReadDTO> result = productReadRepositoryJdbcAdapter.findAll(0, 10, new QueryProducts());

        // THEN
        assertNotNull(result);
        assertNotNull(result.results());
        assertEquals(1, result.results().size());
        assertEquals(0, result.current_page());
        assertEquals(0, result.last_page());
        assertEquals("prod-123", result.results().get(0).getId());
    }

    @Test
    void shouldGetAllProductsSuccessfullyWhenThereIsOneProductWithCurrentPageOfOneValueAndLastPageOfTwoValue() {
        // GIVEN
        List<ProductReadDTO> expectedList = List.of(
            new ProductReadDTO("prod-123", "Electronics", "electronics", "generic description", List.of(), true, List.of(), 0.22, 12, null, null),
            new ProductReadDTO("prod-123", "Nave", "helicopter", "generic description", List.of(), true, List.of(), 0.42, 32, null, null)
        );

        when(namedParameterJdbcTemplate.query(
            Mockito.anyString(),
            Mockito.any(MapSqlParameterSource.class),
            Mockito.any(ProductResultSetExtractor.class)
        )).thenReturn(expectedList);

        when(jdbcTemplate.queryForObject(
            Mockito.anyString(),
            Long.class 
        )).thenReturn(2L);

        // WHEN
        Pagination<ProductReadDTO> result = productReadRepositoryJdbcAdapter.findAll(0, 1, new QueryProducts());

        // THEN
        assertNotNull(result);
        assertNotNull(result.results());
        assertEquals(1, result.results().size());
        assertEquals(0, result.current_page());
        assertEquals(2, result.last_page());
        assertEquals("prod-123", result.results().get(0).getId());
    }

}
