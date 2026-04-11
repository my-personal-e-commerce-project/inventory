package microservice.cloud.inventory.discount.presentation.validate;

import java.time.LocalDateTime;
import java.util.Set;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class DiscountDTO {

    private String id;
    private String name;
    
    @NotEmpty
    private String discountType;
    
    private Double percentageValue;
    private Double decrementValue;

    private Set<String> allowedCategories = null;
    private boolean validAllCategories = false;
    private Double minPrice = null;
    private Double maxPrice = null;
    private int minStock = 0;
    private int maxStock = 0;
    private boolean autoApply = false;

    @NotNull
    @Future
    private LocalDateTime expiredAt;
}
