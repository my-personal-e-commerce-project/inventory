package microservice.cloud.inventory.category.infrastructure.persistence.model;

import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.util.HashSet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table("category")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryEntity {

    @Id
    private String id; 

    private String name;

    private String slug;

    private String parent_id;

    @MappedCollection(idColumn = "category_id")
    @Builder.Default
    private Set<CategoryAttributeEntity> categoryAttributes = new HashSet<>();
}
