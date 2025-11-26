package microservice.cloud.inventory.product.application.dtos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import microservice.cloud.inventory.product.infrastructure.presentation.validate.ProductAttributeValueDTO;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ProductReadDTO {

    @Builder.Default
    private String id = null;

    private String title;

    private String slug;

    private String description;

    private List<String> categories;

    private List<ProductAttributeValueDTO> attributes;

    private double price;

    private int stock;

    private List<String> images;
}
