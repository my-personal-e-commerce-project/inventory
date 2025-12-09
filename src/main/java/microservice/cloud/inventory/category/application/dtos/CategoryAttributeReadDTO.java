package microservice.cloud.inventory.category.application.dtos;
    
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
public class CategoryAttributeReadDTO {

    @Builder.Default
    private String id = null;
  
    private AttributeDefinitionReadDTO attributeDefinition;

    private Boolean is_required;

    private Boolean is_filterable;

    private Boolean is_sortable;
}
