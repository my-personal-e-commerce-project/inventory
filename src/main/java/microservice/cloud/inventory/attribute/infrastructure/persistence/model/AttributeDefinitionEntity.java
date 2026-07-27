package microservice.cloud.inventory.attribute.infrastructure.persistence.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import microservice.cloud.inventory.attribute.domain.entity.AttributeDefinition;

@Table("attributedefinition")
@AllArgsConstructor
@Getter
public class AttributeDefinitionEntity {

    @Id
    private String id;
    private String name;
    private String slug;
    private String type;
    @Column("is_global")
    private boolean isGlobal = false;

    @Version
    private Long version;

    public void updateFromDomain(AttributeDefinition attr) {
        this.name = attr.name();
        this.slug = attr.slug().value();
        this.type = attr.type().toString();
        this.isGlobal = attr.is_global();
    }
}
