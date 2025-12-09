package microservice.cloud.inventory.category.application.dtos;

import java.util.List;

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
public class CategoryReadDTO {

    @Builder.Default
    private String id = null;

    private String name;

    private String slug;

    @Builder.Default
    private String parent_id = null;

    private List<CategoryAttributeReadDTO> categoryAttributes;
}
