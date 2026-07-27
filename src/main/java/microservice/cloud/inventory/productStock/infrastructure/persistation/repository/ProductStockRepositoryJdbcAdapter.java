package microservice.cloud.inventory.productStock.infrastructure.persistation.repository;

import java.util.Map;
import java.util.function.Consumer;

import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
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
    private final JdbcTemplate jdbcTemplate;

    @Transactional(readOnly = true)
    @Override
    public ProductStock findByProductId(Id productId) {
        ProductStockEntity ps = jdbcAggregateTemplate.findById(productId.value(), ProductStockEntity.class);
        return toMap(ps);
    }

    @Transactional
    @Override
    public void save(ProductStock productStock) {
        jdbcAggregateTemplate.insert(factoryProductEntity(productStock));
    }

    @Transactional
    @Override
    public ProductStock updateIfExists(Id productId, Consumer<ProductStock> function) {
        ProductStockEntity ps = jdbcTemplate.queryForObject(
            "SELECT * FROM productstock WHERE product_id = ?",
            new Object[] { productId.value() },
            (rs, rowNum) -> new ProductStockEntity(
                rs.getString("id"),
                rs.getString("product_id"),
                rs.getInt("quantity"),
                rs.getLong("version")
            )
        );

        if (ps == null) throw new DataNotFound("ProductStock not found");

        ProductStock productStock = toMap(ps);

        function.accept(productStock);

        ps.updateFromDomain(productStock);

        jdbcAggregateTemplate.update(ps);

        return productStock;
    }

    private ProductStock toMap(ProductStockEntity productStockEntity) {
        return new ProductStock(Id.fromString(productStockEntity.getId()), Id.fromString(productStockEntity.getProductId()), new Quantity(productStockEntity.getQuantity()));
    }

    private ProductStockEntity factoryProductEntity(ProductStock productStock) {
        return new ProductStockEntity(productStock.id().value(), productStock.productId().value(), productStock.quantity().value(), 1L);
    }
}
