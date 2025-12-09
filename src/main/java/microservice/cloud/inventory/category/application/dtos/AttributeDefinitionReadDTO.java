package microservice.cloud.inventory.category.application.dtos;

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
public class AttributeDefinitionReadDTO {

    @Builder.Default
    public String id = null;

    public String name;
    
    public String slug;

    public String type;

    @Builder.Default
    public Boolean is_global = null;
}
    
