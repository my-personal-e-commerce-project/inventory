package microservice.cloud.inventory.productStock.infrastructure.persistation.repository;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Table("product_stock")
public class ProductStockEntity {

    @Id
    private String id;
    private String productId;
    private int quantity;
}
