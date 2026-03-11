package microservice.cloud.inventory.attribute.infrastructure.persistence.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table("attributedefinition")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class AttributeDefinitionEntity {

    @Id
    private String id;
    private String name;
    private String slug;
   
    private String type;

    @Builder.Default
    private boolean is_global = false;
}
