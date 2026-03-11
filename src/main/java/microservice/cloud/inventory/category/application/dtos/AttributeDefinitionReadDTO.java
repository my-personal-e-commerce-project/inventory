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
    public String id;
    public String name;
    public String slug;
    public String type;
    public Boolean is_global;
}
