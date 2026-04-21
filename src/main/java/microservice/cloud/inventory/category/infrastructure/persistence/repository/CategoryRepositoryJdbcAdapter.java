package microservice.cloud.inventory.category.infrastructure.persistence.repository;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import microservice.cloud.inventory.attribute.domain.entity.AttributeDefinition;
import microservice.cloud.inventory.attribute.domain.value_objects.DataType;
import microservice.cloud.inventory.attribute.infrastructure.persistence.model.AttributeDefinitionEntity;
import microservice.cloud.inventory.attribute.infrastructure.persistence.repository.AttributeDefinitionJdbcRepository;
import microservice.cloud.inventory.category.domain.entity.Category;
import microservice.cloud.inventory.category.domain.entity.CategoryAttribute;
import microservice.cloud.inventory.category.domain.repository.CategoryRepository;
import microservice.cloud.inventory.category.infrastructure.persistence.model.*;
import microservice.cloud.inventory.shared.domain.exception.DataNotFound;
import microservice.cloud.inventory.shared.domain.value_objects.Id;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;

@Repository
@RequiredArgsConstructor
public class CategoryRepositoryJdbcAdapter implements CategoryRepository {

    private final AttributeDefinitionJdbcRepository attributeDefinitionJdbcRepository;
    private final CategoryJdbcRepository categoryJdbcRepository;
    private final JdbcAggregateTemplate aggregateTemplate;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public CategoryAttribute getCategoryAttributeByAttributeDefinitionId(Id id) {
        String query = """
            SELECT 
                ca.id AS attr_id,
                ca.category_id AS attr_cat_id,
                ca.attribute_definition_id AS attr_def_id,
                ca.is_required AS attr_is_required,
                ca.is_sortable AS attr_is_sortable,
                ca.is_filterable AS attr_is_filterable,
                ad.id AS def_id,
                ad.name AS def_name,
                ad.slug AS def_slug,
                ad.type AS def_type,
                ad.is_global AS def_is_global
            FROM categoryattribute ca 
            LEFT JOIN attributedefinition ad 
            ON ca.attribute_definition_id = ad.id 
            WHERE ca.attribute_definition_id = ?
        """;

        CategoryAttributeEntity attr = jdbcTemplate.query(
            query, 
            new CategoryAttributeResultSetExtractor(), 
            id.value()
        );

        return attr == null
            ? null
            : toMap(attr);
    }

    @Override
    public List<CategoryAttribute> 
        getCategoryAttributesWithAttributeDefinitionsByCategoryIds(Set<String> categoriesIds) {

        List<CategoryAttribute> catAttrs = helper_getCategoryAttributesWithAttributeDefinitionsByCategoryIds(categoriesIds)
                .stream()
                .map(entity -> {
                    CategoryAttribute catAttr = toMap(entity);
                    catAttr.load_attribute_definition(
                        new AttributeDefinition(
                            Id.fromString(entity.getAttribute_definition().getId()), 
                            entity.getAttribute_definition().getName(), 
                            Slug.fromString(entity.getAttribute_definition().getSlug()), 
                            DataType.valueOf(entity.getAttribute_definition().getType()), 
                            entity.getAttribute_definition().isGlobal()
                        )
                    );
                    return catAttr;
                }).toList();

        return catAttrs;
    }

    @Override
    public CategoryAttribute getCategoryAttributeById(Id id) {
        CategoryAttributeEntity entity = aggregateTemplate.findById(id.value(), CategoryAttributeEntity.class);

        if(entity == null)
            throw new DataNotFound("Category attribute not found");

        return toMap(entity);
    }

    private Set<CategoryAttributeEntity> helper_getCategoryAttributesWithAttributeDefinitionsByCategoryIds(Set<String> categoriesIds) {
        if (categoriesIds == null || categoriesIds.isEmpty()) return Collections.emptySet();

        String sql = """
            SELECT ca.*, ad.name, ad.slug, ad.type, ad.is_global 
            FROM categoryattribute ca
            JOIN attributedefinition ad ON ca.attribute_definition_id = ad.id
            WHERE ca.category_id IN (:ids)
        """;
        
        MapSqlParameterSource parameters = new MapSqlParameterSource("ids", categoriesIds);
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(jdbcTemplate);

        List<CategoryAttributeEntity> queryResults = template.query(sql, parameters, (rs, rowNum) -> {
            AttributeDefinitionEntity attrDef = new AttributeDefinitionEntity(
                rs.getString("attribute_definition_id"),
                rs.getString("name"),
                rs.getString("slug"),
                rs.getString("type"),
                rs.getBoolean("is_global")
            );

            return new CategoryAttributeEntity(
                rs.getString("id"),
                rs.getString("category_id"),
                attrDef.getId(),
                attrDef,
                rs.getBoolean("is_required"),
                rs.getBoolean("is_filterable"),
                rs.getBoolean("is_sortable")
            );
        });

        return new HashSet<>(queryResults);
    }

    @Override
    public Category findBySlug(Slug slug) {
        return toMap(
            categoryJdbcRepository
                .findBySlug(
                    slug.value()
                )
                .orElseThrow(() -> new DataNotFound("Category not found")));
    }

    

    @Transactional
    @Override
    public void save(Category category) {
        Set<String> definitionIds = category.categoryAttributes().stream()
            .map(attr -> attr.attribute_definition_id().value())
            .distinct()
            .collect(Collectors.toSet());

        long count = attributeDefinitionJdbcRepository.countByIdIn(definitionIds);

        if (count != definitionIds.size()) {
            throw new RuntimeException("One or more attribute definitions are do not exist!");
        }

        if(categoryJdbcRepository.existsBySlug(category.slug().value()))
            throw new RuntimeException("This slug already exists");

        if(categoryJdbcRepository.existsByName(category.name()))
            throw new RuntimeException("This name already exists");
        
        aggregateTemplate.insert(toMap(category));
    }

    @Transactional
    @Override
    public void update(Category category) {
        Set<String> definitionIds = category.categoryAttributes().stream()
            .map(attr -> attr.attribute_definition_id().value())
            .distinct()
            .collect(Collectors.toSet());

        long count = attributeDefinitionJdbcRepository.countByIdIn(definitionIds);

        if (count != definitionIds.size()) {
            throw new RuntimeException("One or more attribute definitions are do not exist!");
        }

        if(categoryJdbcRepository.existsBySlugAndIdNot(category.slug().value(), category.id().value()))
            throw new RuntimeException("This slug already exists");

        if(categoryJdbcRepository.existsByNameAndIdNot(category.name(), category.id().value()))
            throw new RuntimeException("This name already exists");

        if(!categoryJdbcRepository.existsById(category.parent_id().value()))
            throw new RuntimeException("The category %s does not exists".formatted(category.parent_id().value()));
        
        aggregateTemplate.update(toMap(category));
    }

    @Transactional
    @Override
    public void delete(Category category) {
        aggregateTemplate.delete(toMap(category));
    }

    private Category toMap(CategoryEntity entity) {
        return new Category(
            Id.fromString(entity.getId()), 
            entity.getName(), 
            Slug.fromString(entity.getSlug()), 
            entity.getParent_id() != null
            ? Id.fromString(entity.getParent_id())
            : null,
            entity.getCategoryAttributes()
                    .stream()
                    .map(attr -> {
                    return toMap(attr);
                }).collect(Collectors.toSet())
        );
    }

    private CategoryAttribute toMap(CategoryAttributeEntity entity) {
        CategoryAttribute catAttr = new CategoryAttribute(
            Id.fromString(entity.getId()),
            Id.fromString(entity.getAttribute_definition_id()),
            entity.getIs_required(), 
            entity.getIs_filterable(), 
            entity.getIs_sortable()
        );
       
        return catAttr;
    }

    private CategoryEntity toMap(Category entity) {
        return new CategoryEntity(
            entity.id().value(), 
            entity.name(), 
            entity.slug().value(), 
            entity.parent_id() != null? entity.parent_id().value(): null,
            entity.categoryAttributes()
                .stream()
                .map(attr -> toMap(entity.id(), attr)
            ).collect(Collectors.toSet())
        );
    }
    
    private CategoryAttributeEntity toMap(Id cat, CategoryAttribute entity) {
        return new CategoryAttributeEntity(
            entity.id().value(),
            cat.value(),
            entity.attribute_definition_id().value(),
            null,
            entity.is_required(), 
            entity.is_filterable(), 
            entity.is_sortable()
        );
    }
}
