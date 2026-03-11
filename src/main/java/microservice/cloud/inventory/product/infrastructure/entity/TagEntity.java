package microservice.cloud.inventory.product.infrastructure.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table("tag")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TagEntity {

    @Id
    private String id;

    private String name;

    private ProductEntity product;
}
