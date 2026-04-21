package microservice.cloud.inventory.product.infrastructure.persistence.repository;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import microservice.cloud.inventory.product.application.dtos.ProductReadDTO;
import microservice.cloud.inventory.product.application.ports.out.ProductReadRepository;
import microservice.cloud.inventory.shared.application.dto.Pagination;

@Repository
@RequiredArgsConstructor
public class ProductReadRepositoryJdbcAdapter implements ProductReadRepository {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public Pagination<ProductReadDTO> findAll(int page, int limit) {
        int offset = page * limit;
        
        MapSqlParameterSource params = new MapSqlParameterSource()
            .addValue("limit", limit)
            .addValue("offset", offset);

        List<ProductReadDTO> products = namedParameterJdbcTemplate.query(
            """
            SELECT 
                p.id AS prod_id, 
                p.title, 
                p.slug, 
                p.description, 
                p.images, 
                p.tags, 
                p.price, 
                p.stock,
                pav.id AS val_id,
                pav.string_value, 
                pav.integer_value, 
                pav.double_value, 
                pav.boolean_value,
                ad.slug AS attr_slug,
                ad.id AS attr_id,
                pd.discount_id AS discount_id,
                (SELECT string_agg(category_id, ',') FROM product_categories WHERE product_id = p.id) AS all_categories
            FROM (
                SELECT * FROM products 
                ORDER BY id 
                LIMIT :limit OFFSET :offset
            ) p
            LEFT JOIN product_attribute_values pav ON p.id = pav.product_id
            LEFT JOIN attributedefinition ad ON pav.attribute_definition_id = ad.id
            LEFT JOIN product_discounts pd ON pd.product_id = p.id
            """,
            params,
            new ProductResultSetExtractor()
        );

        long total = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM products;", Long.class);

        int totalPages = (limit == 0) ? 1 : (int) Math.ceil((double) total / limit);
    
        int last_page = Math.max(0, totalPages - 1);

        return new Pagination<ProductReadDTO>(products, last_page, page);
    }
}
