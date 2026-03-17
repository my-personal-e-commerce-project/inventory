package microservice.cloud.inventory.category.infrastructure.persistence.model;

import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.util.HashSet;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Table("category")
@Getter
@AllArgsConstructor
public class CategoryEntity {

    @Id
    private String id; 

    private String name;

    private String slug;

    private String parent_id;

    @MappedCollection(idColumn = "category_id")
    private Set<CategoryAttributeEntity> categoryAttributes = new HashSet<>();
}
