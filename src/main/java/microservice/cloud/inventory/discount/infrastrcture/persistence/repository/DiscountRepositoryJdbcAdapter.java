package microservice.cloud.inventory.discount.infrastrcture.persistence.repository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.stereotype.Repository;

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
import microservice.cloud.inventory.shared.domain.value_objects.Id;

@RequiredArgsConstructor
@Repository
public class DiscountRepositoryJdbcAdapter implements DiscountRepository {

    private final JdbcAggregateTemplate jdbcAggregateTemplate;
    private final DiscountJdbcRepository discountJdbcRepository;
    private final CategoryJdbcRepository categoryJdbcRepository;

    @Override
    public void save(Discount discount) {
        if(!categoryJdbcRepository.countByIdIn(discount.allowedCategories()))
            throw new RuntimeException("Not all provided category ids are valid");

        jdbcAggregateTemplate.insert(toMap(discount));
    }

    @Override
    public void update(Discount discount) {
        if(!categoryJdbcRepository.countByIdIn(discount.allowedCategories()))
            throw new RuntimeException("Not all provided category ids are valid");

        jdbcAggregateTemplate.update(toMap(discount));
    }

    @Override
    public void delete(Id id) {
        jdbcAggregateTemplate.deleteById(id, DiscountEntity.class);
    }

    @Override
    public void applyAutomaticDiscounts(Product product) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public List<Discount> getDiscountsByIds(Set<String> discountIds) {
        List<DiscountEntity> discounts = 
            discountJdbcRepository.countByIdIn(discountIds);

            return discounts.stream().map(this::toMap).toList();
    }

    private Discount toMap(DiscountEntity entity) {
    
        return new Discount(
            Id.fromString(entity.getId()), 
            entity.getName(), 
            DiscountType.valueOf(entity.getDiscountType()), 
            new Percentage(entity.getPercentageValue()), 
            entity.getDecrementValue(), 
            entity.getAllowedCategories() == null
                ? null
                : entity.getAllowedCategories()
                    .stream()
                    .map(c -> c.categoryId())
                    .collect(Collectors.toSet()), 
            entity.isValidAllCategories(), 
            new Price(entity.getMinPrice()), 
            new Price(entity.getMaxPrice()), 
            new Quantity(entity.getMinStock()), 
            new Quantity(entity.getMaxStock()), 
            entity.isAutoApply(), 
            entity.getExpiredAt()
        );
    }

    private DiscountEntity toMap(Discount discount) {
    
        return new DiscountEntity(
            discount.id().value(), 
            discount.name(), 
            discount.discountType().toString(), 
            discount.percentageValue().value(), 
            discount.decrementValue(), 
            discount.allowedCategories() == null
                ? null
                : discount.allowedCategories()
                    .stream()
                    .map((String c) -> new DiscountEntity.DiscountCategoryReference(c))
                    .collect(Collectors.toSet()), 
            discount.validAllCategories(), 
            discount.minPrice().value(), 
            discount.maxPrice().value(), 
            discount.minStock().value(), 
            discount.maxStock().value(), 
            discount.autoApply(), 
            discount.expiredAt()
        );
    }
}
