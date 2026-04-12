package microservice.cloud.inventory.product.infrastructure.presentation.validate;

import java.util.List;
import java.util.Set;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class ProductDTO {

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
    private Set<String> categories;

    @Valid
    private List<ProductAttributeValueDTO> attributes;

    private Set<String> discounts;

    @NotNull
    private Double price;

    @NotNull
    private int stock;

    private Set<String> tags;
}
