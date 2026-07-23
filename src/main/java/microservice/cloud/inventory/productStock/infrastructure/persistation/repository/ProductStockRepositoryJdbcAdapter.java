package microservice.cloud.inventory.productStock.infrastructure.persistation.repository;

import java.util.List;
import java.util.function.Consumer;

import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import microservice.cloud.inventory.product.domain.value_objects.Quantity;
import microservice.cloud.inventory.productStock.domain.entity.ProductStock;
import microservice.cloud.inventory.productStock.domain.repository.ProductStockRepository;
import microservice.cloud.inventory.shared.domain.exception.DataNotFound;
import microservice.cloud.inventory.shared.domain.value_objects.Id;

@RequiredArgsConstructor
@Repository
public class ProductStockRepositoryJdbcAdapter implements ProductStockRepository {

    private final JdbcAggregateTemplate jdbcAggregateTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Transactional
    @Override
    public void save(ProductStock productStock) {
        jdbcAggregateTemplate.insert(toMap(productStock));
    }

    @Transactional
    @Override
    public void updatePessimistic(Id productId, Consumer<ProductStock> function) {
       
        ProductStock productStock = findByProductIdForUpdate(productId.value());

        function.accept(productStock);

        jdbcAggregateTemplate.update(toMap(productStock));
    }

    private ProductStock findByProductIdForUpdate(String productId) {
        String sql = "SELECT * FROM product_stock WHERE product_id = :productId FOR UPDATE";
        MapSqlParameterSource params = new MapSqlParameterSource("productId", productId);

        List<ProductStockEntity> result = namedParameterJdbcTemplate.query(
            sql, 
            params, 
            new BeanPropertyRowMapper<>(ProductStockEntity.class)
        );

        ProductStockEntity coupon = result.stream()
            .findFirst()
            .orElseThrow(() -> new DataNotFound("Coupon not found"));

        return toMap(coupon);
    }

    private ProductStock toMap(ProductStockEntity productStockEntity) {
        return new ProductStock(Id.fromString(productStockEntity.getId()), Id.fromString(productStockEntity.getProductId()), new Quantity(productStockEntity.getQuantity()));
    }

    private ProductStockEntity toMap(ProductStock productStock) {
        return new ProductStockEntity(productStock.id().value(), productStock.productId().value(), productStock.quantity().value());
    }
}
