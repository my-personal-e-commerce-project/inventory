package microservice.cloud.inventory.product.application.dtos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ProductReadDTO {
    private String id;
    private String title;
    private String slug;
    private String description;
    private List<String> categories;
    private boolean isActive;
    private List<ProductAttributeValueReadDTO> attributes;
    private Double price;
    private int stock;
    private List<String> images;
    private List<String> tags;
}
