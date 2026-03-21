package microservice.cloud.inventory.category.infrastructure.persistence.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import microservice.cloud.inventory.attribute.infrastructure.persistence.model.AttributeDefinitionEntity;

@Table("categoryattribute")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class CategoryAttributeEntity {

    @Id
    private String id;

    private String category_id;

    private String attribute_definition_id;

    @Transient
    private AttributeDefinitionEntity attribute_definition;

    private Boolean is_required;

    private Boolean is_filterable;

    private Boolean is_sortable;
}
