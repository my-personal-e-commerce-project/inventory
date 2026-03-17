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
    String id;
    String title;
    String slug;
    String description;
    List<String> categories;
    List<ProductAttributeValueReadDTO> attributes;
    Double price;
    int stock;
    List<String> images;
    List<String> tags;
}
