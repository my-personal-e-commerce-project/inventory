package microservice.cloud.inventory.product.infrastructure.persistence.repository;

import java.sql.Types;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import microservice.cloud.inventory.product.application.dtos.ProductReadDTO;
import microservice.cloud.inventory.product.application.dtos.QueryProducts;
import microservice.cloud.inventory.product.application.ports.out.ProductReadRepository;
import microservice.cloud.inventory.shared.application.dto.Pagination;

@Repository
@RequiredArgsConstructor
public class ProductReadRepositoryJdbcAdapter implements ProductReadRepository {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public Pagination<ProductReadDTO> findAll(int page, int limit, QueryProducts query) {
        int offset = page * limit;
       
        boolean noFilter = (query.categories() == null || query.categories().isEmpty());

        MapSqlParameterSource params = new MapSqlParameterSource()
            .addValue("limit", limit)
            .addValue("offset", offset)
            .addValue("search", query.search(), Types.VARCHAR)
            .addValue("thereCategories", noFilter)
            .addValue("categories", noFilter ? null : query.categories().toArray(new String[0]))
            .addValue("minPrice", query.minPrice(), Types.DECIMAL)
            .addValue("maxPrice", query.maxPrice(), Types.DECIMAL)
            .addValue("minStock", query.minStock(), Types.INTEGER)
            .addValue("maxStock", query.maxStock(), Types.INTEGER);

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
                p.is_active AS isActive,
                pav.id AS val_id,
                pav.string_value, 
                pav.integer_value, 
                pav.double_value, 
                pav.boolean_value,
                ad.slug AS attr_slug,
                ad.id AS attr_id,
                (SELECT string_agg(category_id, ',') FROM product_categories WHERE product_id = p.id) AS all_categories
            FROM (
                SELECT * FROM products 
                ORDER BY id 
                LIMIT :limit OFFSET :offset
            ) p
            LEFT JOIN product_attribute_values pav ON p.id = pav.product_id
            LEFT JOIN attributedefinition ad ON pav.attribute_definition_id = ad.id
            LEFT JOIN product_categories pc ON p.id = pc.product_id
            WHERE ((:search IS NULL OR :search = '')
                OR (
                     p.title ILIKE '%' || :search || '%'
                    OR p.description ILIKE '%' || :search || '%'
                    OR p.slug ILIKE '%' || :search || '%'
                    OR p.description ILIKE '%' || :search || '%'
                    OR pav.string_value ILIKE '%' || :search || '%'
                    OR pav.integer_value::text ILIKE '%' || :search || '%'
                    OR pav.double_value::text ILIKE '%' || :search || '%'
                    OR pav.boolean_value::text ILIKE '%' || :search || '%'
                )
            ) AND (
                (:thereCategories)
                OR p.id IN (
                    SELECT product_id 
                    FROM product_categories 
                    WHERE category_id = ANY(CAST(:categories AS text[]))
                )
            ) AND (
                (:minPrice IS NULL OR p.price >= :minPrice)
            ) AND (
                (:maxPrice IS NULL OR p.price <= :maxPrice)
            ) AND (
                (:minStock IS NULL OR p.stock >= :minStock)
            ) AND (
                (:maxStock IS NULL OR p.stock <= :maxStock)
            );
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
