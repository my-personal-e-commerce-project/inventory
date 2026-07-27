package microservice.cloud.inventory.productStock.infrastructure.persistation.repository;

import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
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
    public void updateIfExists(Id productId, ProductStock productStock) {
        ProductStockEntity ps = jdbcAggregateTemplate.findById(productId.value(), ProductStockEntity.class);

        if(ps == null) {
            throw new DataNotFound("Product stock not found");
        }

        ps.updateFromDomain(productStock);

        jdbcAggregateTemplate.update(factoryProductEntity(productStock));
    }

    private ProductStock toMap(ProductStockEntity productStockEntity) {
        return new ProductStock(Id.fromString(productStockEntity.getId()), Id.fromString(productStockEntity.getProductId()), new Quantity(productStockEntity.getQuantity()));
    }

    private ProductStockEntity factoryProductEntity(ProductStock productStock) {
        return new ProductStockEntity(productStock.id().value(), productStock.productId().value(), productStock.quantity().value(), 1L);
    }
}
