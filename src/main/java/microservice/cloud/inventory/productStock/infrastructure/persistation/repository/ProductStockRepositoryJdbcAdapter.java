package microservice.cloud.inventory.productStock.infrastructure.persistation.repository;

import java.beans.Transient;
import java.util.List;
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
        String sql = "SELECT id, product_id, quantity, version FROM product_stock WHERE product_id = ?";
        List<ProductStockEntity> list = jdbcTemplate.query(sql, (rs, rowNum) -> new ProductStockEntity(
            rs.getString("id"),
            rs.getString("product_id"),
            rs.getInt("quantity"),
            rs.getLong("version")
        ), productId.value());

        if (list.isEmpty()) {
            throw new DataNotFound("Product stock not found for product id: " + productId.value());
        }
        return toMap(list.get(0));
    }

    @Transactional
    @Override
    public void save(ProductStock productStock) {
        jdbcAggregateTemplate.insert(factoryProductEntity(productStock));
    }

    @Transactional
    @Override
    public void incrementStock(Id id, int value) {
        String sql = "UPDATE product_stock SET quantity = quantity + ? WHERE product_id = ?";
        jdbcTemplate.update(sql, value, id.value());    
    }

    @Transactional
    @Override
    public void decrementStock(Id id, int value) {
        String sql = "UPDATE product_stock SET quantity = quantity - ? WHERE product_id = ?";
        jdbcTemplate.update(sql, value, id.value());    
    }

    private ProductStock toMap(ProductStockEntity productStockEntity) {
        return new ProductStock(Id.fromString(productStockEntity.getId()), Id.fromString(productStockEntity.getProductId()), new Quantity(productStockEntity.getQuantity()));
    }

    private ProductStockEntity factoryProductEntity(ProductStock productStock) {
        return new ProductStockEntity(productStock.id().value(), productStock.productId().value(), productStock.quantity().value(), 1L);
    }
}
