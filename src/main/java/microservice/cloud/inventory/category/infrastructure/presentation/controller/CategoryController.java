package microservice.cloud.inventory.category.infrastructure.presentation.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import microservice.cloud.inventory.attribute.domain.entity.AttributeDefinition;
import microservice.cloud.inventory.attribute.domain.value_objects.DataType;
import microservice.cloud.inventory.category.application.ports.in.CreateCategoryAttributeUseCasePort;
import microservice.cloud.inventory.category.application.ports.in.CreateCategoryUseCasePort;
import microservice.cloud.inventory.category.application.ports.in.DeleteCategoryAttributeUseCasePort;
import microservice.cloud.inventory.category.application.ports.in.DeleteCategoryUseCasePort;
import microservice.cloud.inventory.category.application.ports.in.ListCategoryUseCasePort;
import microservice.cloud.inventory.category.application.ports.in.UpdateCategoryUseCasePort;
import microservice.cloud.inventory.category.domain.entity.Category;
import microservice.cloud.inventory.category.domain.entity.CategoryAttribute;
import microservice.cloud.inventory.category.infrastructure.dto.ResponsePayload;
import microservice.cloud.inventory.category.infrastructure.presentation.validate.AttributeDefinitionDTO;
import microservice.cloud.inventory.category.infrastructure.presentation.validate.CategoryAttributeDTO;
import microservice.cloud.inventory.category.infrastructure.presentation.validate.CategoryDTO;
import microservice.cloud.inventory.category.infrastructure.presentation.validate.UpdateCategoryAttributeDTO;
import microservice.cloud.inventory.category.infrastructure.presentation.validate.UpdateCategoryDTO;
import microservice.cloud.inventory.shared.application.dto.Pagination;
import microservice.cloud.inventory.shared.domain.value_objects.Id;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final ListCategoryUseCasePort listCategoryUseCasePort;
    private final CreateCategoryUseCasePort createCategoryUseCasePort;
    private final UpdateCategoryUseCasePort updateCategoryUseCasePort;
    private final DeleteCategoryUseCasePort deleteCategoryUseCasePort;

    private final CreateCategoryAttributeUseCasePort createCategoryAttributeUseCasePort; 
    private final DeleteCategoryAttributeUseCasePort deleteCategoryAttributeUseCasePort; 

    @GetMapping
    public ResponseEntity<ResponsePayload<Pagination<CategoryDTO>>> getAttributes(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        Pagination<Category> categories = listCategoryUseCasePort.execute(0, 10);
        Pagination<CategoryDTO> categoriesDTO = new Pagination<>(
            categories.getResults().stream().map(this::toMap).toList(),
            categories.getLast_page(),
            categories.getCurrent_page()
        );

        return new ResponseEntity<>(
            ResponsePayload.<Pagination<CategoryDTO>>builder()
                .message("Attributes retrieved successfully")
                .payload(categoriesDTO)
                .build(),
                HttpStatus.OK
        );
    }

    @PostMapping
    public ResponseEntity<ResponsePayload<CategoryDTO>> createCategory(
        @Valid @RequestBody CategoryDTO category
    ) {
        Slug slug = new Slug(category.getSlug());

        createCategoryUseCasePort.execute(
            category.getName(),
            slug,
            category.getParent_id() == null? null: new Id(category.getParent_id()),
            category.getCategoryAttributes() == null? null: 
            category.getCategoryAttributes().stream().map(attr -> {
                attr.setId(Id.generate().value());
                attr.getAttributeDefinition().setId(Id.generate().value());
                return toMap(attr);
            }).toList() 
        );

        return new ResponseEntity<>(
            ResponsePayload.<CategoryDTO>builder()
                .message("Category created successfully")
                .payload(category)
                .build(),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponsePayload<UpdateCategoryDTO>> updateCategory(
        @PathVariable String id,
        @Valid @RequestBody UpdateCategoryDTO category
    ) {
        category.setId(id);

        List<CategoryAttribute> attrs = category.getCategoryAttributes()
            .stream()
            .map(attr -> {
                return toMap(attr);
            })
            .toList();

        updateCategoryUseCasePort.execute(
            new Id(id),
            category.getName(), 
            new Slug(category.getSlug()), 
            new Id(category.getParent_id()), 
            attrs
        );

        return new ResponseEntity<>(
            ResponsePayload.<UpdateCategoryDTO>builder()
                .message("Category updated successfully")
                .payload(category)
                .build(),
                HttpStatus.OK
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(
        @PathVariable String id
    ) {
        deleteCategoryUseCasePort.execute(
            new Id(id)
        );

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/attributes")
    public ResponseEntity<ResponsePayload<CategoryAttributeDTO>> createCategoryAttribute(
        @PathVariable String id,
        @Valid @RequestBody CategoryAttributeDTO categoryAttribute
    ) {
        categoryAttribute.setId(Id.generate().value());

        CategoryAttribute attr = toMap(categoryAttribute);
        
        createCategoryAttributeUseCasePort.execute(new Id(id), attr);

        return new ResponseEntity<>(
            ResponsePayload.<CategoryAttributeDTO>builder()
                .message("Category attribute added successfully")
                .payload(categoryAttribute)
                .build(),
                HttpStatus.OK
        );    
    }

    @DeleteMapping("/{id}/attributes/{attr_id}")
    public ResponseEntity<?> deleteCategoryAttribute(
        @PathVariable String id,
        @PathVariable String attr_id
    ) {
        deleteCategoryAttributeUseCasePort.execute(new Id(id), new Id(attr_id));

        return ResponseEntity.noContent().build();
    }

    private CategoryAttribute toMap(UpdateCategoryAttributeDTO attr) {

        return new CategoryAttribute(
            new Id(attr.getId()), 
            new AttributeDefinition(
                new Id(attr.getAttributeDefinition().getId()), 
                attr.getAttributeDefinition().getName(), 
                new Slug(attr.getAttributeDefinition().getSlug()), 
                DataType.valueOf(attr.getAttributeDefinition().getType()), 
                false
            ),
            attr.getIs_required(), 
            attr.getIs_filterable(), 
            attr.getIs_sortable()
        );
    }

    private CategoryAttribute toMap(CategoryAttributeDTO categoryAttribute) {
        return new CategoryAttribute(
            new Id(categoryAttribute.getId()),
            new AttributeDefinition(
                Id.generate(),
                categoryAttribute.getAttributeDefinition().getName(),
                new Slug(categoryAttribute.getAttributeDefinition().getSlug()),
                DataType.valueOf(categoryAttribute.getAttributeDefinition().getType()),
               false 
            ),
            categoryAttribute.getIs_required(),
            categoryAttribute.getIs_filterable(),
            categoryAttribute.getIs_sortable() 
        );
    }

    private CategoryDTO toMap(Category category) {

        List<CategoryAttributeDTO> categoryAttributesDTO = 
            category.categoryAttributes() == null? null:
            category.categoryAttributes().stream().<CategoryAttributeDTO>map(
            categoryAttribute -> CategoryAttributeDTO.builder()
                .id(categoryAttribute.id().value())
                .attributeDefinition(
                    AttributeDefinitionDTO.builder()
                        .id(categoryAttribute.attribute_definition().id().value())
                        .name(categoryAttribute.attribute_definition().name())
                        .slug(categoryAttribute.attribute_definition().slug().value())
                        .type(categoryAttribute.attribute_definition().type().toString())
                        .is_global(false)
                        .build()
                )
                .is_required(categoryAttribute.is_required())
                .is_filterable(categoryAttribute.is_filterable())
                .is_sortable(categoryAttribute.is_sortable())
                .build()
        ).toList();

        return CategoryDTO.builder()
            .id(category.id().value())
            .name(category.name())
            .slug(category.slug().value())
            .parent_id(category.parent_id() == null ? null : category.parent_id().value())
            .categoryAttributes(categoryAttributesDTO)
            .build();
    }
}
