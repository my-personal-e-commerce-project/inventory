package microservice.cloud.inventory.discount.infrastrcture.persistence.repository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import microservice.cloud.inventory.category.infrastructure.persistence.repository.CategoryJdbcRepository;
import microservice.cloud.inventory.discount.domain.entity.Discount;
import microservice.cloud.inventory.discount.domain.repository.DiscountRepository;
import microservice.cloud.inventory.discount.domain.value_objects.DiscountType;
import microservice.cloud.inventory.discount.domain.value_objects.Percentage;
import microservice.cloud.inventory.discount.infrastrcture.persistence.model.DiscountEntity;
import microservice.cloud.inventory.product.domain.entity.Product;
import microservice.cloud.inventory.product.domain.value_objects.Price;
import microservice.cloud.inventory.product.domain.value_objects.Quantity;
import microservice.cloud.inventory.shared.domain.exception.DataNotFound;
import microservice.cloud.inventory.shared.domain.value_objects.Id;

@RequiredArgsConstructor
@Repository
public class DiscountRepositoryJdbcAdapter implements DiscountRepository {

    private final JdbcAggregateTemplate jdbcAggregateTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final DiscountJdbcRepository discountJdbcRepository;
    private final CategoryJdbcRepository categoryJdbcRepository;

    @Transactional
    @Override
    public void save(Discount discount) {
        if(
            discount.allowedCategories() != null 
            && discount.allowedCategories().size() == 0 
            && categoryJdbcRepository.countByIdIn(discount.allowedCategories()) == 0
        )
            throw new RuntimeException("Not all provided category ids are valid");

        jdbcAggregateTemplate.insert(toMap(discount));
    }

    @Transactional
    @Override
    public void update(Discount discount) {
        if(
            discount.allowedCategories() != null 
            && discount.allowedCategories().size() == 0 
            && categoryJdbcRepository.countByIdIn(discount.allowedCategories()) == 0
        )            
            throw new RuntimeException("Not all provided category ids are valid");

        jdbcAggregateTemplate.update(toMap(discount));
    }

    @Transactional
    @Override
    public void delete(Discount discount) {
        jdbcAggregateTemplate.deleteById(discount.id().value(), DiscountEntity.class);
    }

    @Override
    public Discount getById(Id id) {
        DiscountEntity entity = jdbcAggregateTemplate.findById(id.value(), DiscountEntity.class);

        if(entity == null)
            throw new DataNotFound("Discount not found");

        return toMap(entity);
    }

    @Transactional
    @Override
    public void applyDiscountsToThisProduct(Product product) {
        String sql = """
            INSERT INTO product_discounts (product_id, discount_id)
            SELECT DISTINCT :productId, d.id
            FROM discounts d
            WHERE 
                d.auto_apply = true
                AND d.expired_at > NOW()
                AND (d.min_price IS NULL OR :productPrice >= d.min_price)
                AND (d.max_price IS NULL OR :productPrice <= d.max_price)
                AND (d.min_stock IS NULL OR :productStock >= d.min_stock)
                AND (d.max_stock IS NULL OR :productStock <= d.max_stock)
                AND (
                    d.global_categories = true 
                    OR EXISTS (
                        SELECT 1 
                        FROM discount_categories dc 
                        WHERE dc.discount_id = d.id 
                        AND dc.category_id IN (:productCategoryIds)
                    )
                );
            """;

            System.out.println("hola, test");
            System.out.println("hola, test");
            System.out.println("hola, test");
            System.out.println("hola, test");
        MapSqlParameterSource params = new MapSqlParameterSource()
            .addValue("productId", product.id().value())
            .addValue("productCategoryIds", product.categories())
            .addValue("productPrice", product.price().value())
            .addValue("productStock", product.stock().value());

        namedParameterJdbcTemplate.update(sql, params);
    }

    @Override
    public List<Discount> getDiscountsByIds(Set<String> discountIds) {
        List<DiscountEntity> discounts = 
            discountJdbcRepository.findByIdIn(discountIds);

        return discounts.stream().map(this::toMap).toList();
    }

    private Discount toMap(DiscountEntity entity) {
    
        return new Discount(
            Id.fromString(entity.getId()), 
            entity.getName(), 
            DiscountType.valueOf(entity.getDiscountType()), 
            new Percentage(entity.getPercentageValue()), 
            entity.getDecrementValue() == null? null: new Price(entity.getDecrementValue()), 
            entity.getAllowedCategories() == null
                ? null
                : entity.getAllowedCategories()
                    .stream()
                    .map(c -> c.categoryId())
                    .collect(Collectors.toSet()), 
            entity.isGlobalCategories(), 
            new Price(entity.getMinPrice()), 
            new Price(entity.getMaxPrice()), 
            new Quantity(entity.getMinStock()), 
            new Quantity(entity.getMaxStock()), 
            entity.isAutoApply(),
            false,
            entity.getExpiredAt()
        );
    }

    private DiscountEntity toMap(Discount discount) {
    
        return new DiscountEntity(
            discount.id().value(), 
            discount.name(), 
            discount.discountType().toString(), 
            discount.percentageValue().value(), 
            discount.decrementValue() == null? null: discount.decrementValue().value(),
            discount.allowedCategories() == null
                ? null
                : discount.allowedCategories()
                    .stream()
                    .map((String c) -> new DiscountEntity.DiscountCategoryReference(c))
                    .collect(Collectors.toSet()), 
            discount.globalCategories(), 
            discount.minPrice().value(), 
            discount.maxPrice().value(), 
            discount.minStock().value(), 
            discount.maxStock().value(), 
            discount.autoApply(), 
            discount.expiredAt()
        );
    }
}
