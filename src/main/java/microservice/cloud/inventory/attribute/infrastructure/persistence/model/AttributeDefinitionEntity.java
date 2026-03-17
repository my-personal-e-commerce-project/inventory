package microservice.cloud.inventory.attribute.infrastructure.persistence.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Table("attributedefinition")
@AllArgsConstructor
@Getter
public class AttributeDefinitionEntity {

    @Id
    private String id;
    private String name;
    private String slug;
    private String type;
    private boolean is_global = false;
}
