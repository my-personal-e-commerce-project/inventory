package microservice.cloud.inventory.category.application.dtos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class CategoryReadDTO {

    private String id;
    private String name;
    private String slug;
    private String parent_id;
    private List<CategoryAttributeReadDTO> categoryAttributes;
}
