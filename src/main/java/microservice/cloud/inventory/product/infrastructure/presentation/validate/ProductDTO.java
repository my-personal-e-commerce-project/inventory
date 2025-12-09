package microservice.cloud.inventory.product.infrastructure.presentation.validate;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ProductDTO {

    @Builder.Default
    private String id = null;

    @NotNull
    @NotEmpty
    private String title;

    @NotNull
    @NotEmpty
    private String slug;

    @NotNull
    @NotEmpty
    private String description;

    @NotNull
    @NotEmpty
    private List<String> categories;

    @Valid
    @NotNull
    @NotEmpty
    private List<ProductAttributeValueDTO> attributes;

    @NotNull
    private double price;

    @NotNull
    private int stock;

    private List<String> images;

    @NotNull
    private List<String> tags;
}
