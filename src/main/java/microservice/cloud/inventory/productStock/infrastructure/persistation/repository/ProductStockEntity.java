package microservice.cloud.inventory.productStock.infrastructure.persistation.repository;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import microservice.cloud.inventory.productStock.domain.entity.ProductStock;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Table("product_stock")
public class ProductStockEntity {

    @Id
    private String id;
    private String productId;
    private int quantity;

    @Version
    private Long version;

    public void updateFromDomain(ProductStock productStock) {
        this.quantity = productStock.quantity().value();
    }
}
