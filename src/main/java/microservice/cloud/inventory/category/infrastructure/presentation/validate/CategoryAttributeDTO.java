package microservice.cloud.inventory.category.infrastructure.presentation.validate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryAttributeDTO {

    public String attribute_definition_id;
    public Boolean is_required;
    public Boolean is_filterable;
    public Boolean is_sortable;
}
