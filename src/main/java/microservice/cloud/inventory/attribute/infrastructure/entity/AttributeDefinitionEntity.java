package microservice.cloud.inventory.attribute.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "attribute_definition")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class AttributeDefinitionEntity {

    @Id
    @Column(name = "id", unique = true, nullable = false, updatable = false)
    private String id;
    private String name;
    private String slug;
    private String type;
    private Boolean is_global;
}
