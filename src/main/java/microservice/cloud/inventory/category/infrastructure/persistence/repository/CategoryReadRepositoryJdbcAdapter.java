package microservice.cloud.inventory.category.infrastructure.persistence.repository;

import java.util.List;
import java.util.Set;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import microservice.cloud.inventory.category.application.dtos.CategoryReadDTO;
import microservice.cloud.inventory.category.application.ports.out.CategoryReadRepository;
import microservice.cloud.inventory.shared.application.dto.Pagination;

@Repository
@RequiredArgsConstructor
public class CategoryReadRepositoryJdbcAdapter implements CategoryReadRepository {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public List<CategoryReadDTO> getCategoriesByIds(Set<String> ids) {
       
        MapSqlParameterSource params = new MapSqlParameterSource()
            .addValue("ids", ids);

        List<CategoryReadDTO> categories = namedParameterJdbcTemplate.query(
            """
            SELECT 
                c.id AS cat_id, 
                c.name AS cat_name, 
                c.slug AS cat_slug, 
                c.parent_id AS cat_parent_id,
                p.slug AS cat_parent_slug, 
                ca.id AS attr_id, 
                ca.is_required AS attr_is_required, 
                ca.is_sortable AS attr_is_sortable, 
                ca.is_filterable AS attr_is_filterable,
                ad.id AS def_id, 
                ad.name AS def_name, 
                ad.slug AS def_slug, 
                ad.type AS def_type, 
                ad.is_global AS def_is_global
            FROM (
                SELECT * FROM category
                WHERE id IN (:ids)
            ) c
            LEFT JOIN category p ON c.parent_id = p.id 
            LEFT JOIN categoryattribute ca ON c.id = ca.category_id
            LEFT JOIN attributedefinition ad ON ca.attribute_definition_id = ad.id

            """,
            params,
            new CategoryReadResultSetExtractor()
        );

        return categories;
    }

    @Override
    public Pagination<CategoryReadDTO> findAll(int page, int limit) {
        int offset = page * limit;
        
        MapSqlParameterSource params = new MapSqlParameterSource()
            .addValue("limit", limit)
            .addValue("offset", offset);

        List<CategoryReadDTO> categories = namedParameterJdbcTemplate.query(
            """
            SELECT 
                c.id AS cat_id, 
                c.name AS cat_name, 
                c.slug AS cat_slug, 
                c.parent_id AS cat_parent_id,
                p.slug AS cat_parent_slug, 
                ca.id AS attr_id, 
                ca.is_required AS attr_is_required, 
                ca.is_sortable AS attr_is_sortable, 
                ca.is_filterable AS attr_is_filterable,
                ad.id AS def_id, 
                ad.name AS def_name, 
                ad.slug AS def_slug, 
                ad.type AS def_type, 
                ad.is_global AS def_is_global
            FROM (
                SELECT * FROM category 
                LIMIT :limit OFFSET :offset
            ) c
            LEFT JOIN category p ON c.parent_id = p.id 
            LEFT JOIN categoryattribute ca ON c.id = ca.category_id
            LEFT JOIN attributedefinition ad ON ca.attribute_definition_id = ad.id
            """,
            params,
            new CategoryReadResultSetExtractor()
        );

        long total = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM category;", Long.class);

        int totalPages = (limit == 0) ? 1 : (int) Math.ceil((double) total / limit);
    
        int last_page = Math.max(0, totalPages - 1);

        return new Pagination<CategoryReadDTO>(categories, last_page, page);
    }
}
