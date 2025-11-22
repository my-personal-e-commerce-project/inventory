package microservice.cloud.inventory.category.infrastructure.presentation.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
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
import microservice.cloud.inventory.category.application.ports.in.CreateCategoryUseCasePort;
import microservice.cloud.inventory.category.application.ports.in.DeleteCategoryUseCasePort;
import microservice.cloud.inventory.category.application.ports.in.ListCategoryUseCasePort;
import microservice.cloud.inventory.category.application.ports.in.UpdateCategoryUseCasePort;
import microservice.cloud.inventory.category.domain.entity.Category;
import microservice.cloud.inventory.category.domain.entity.CategoryAttribute;
import microservice.cloud.inventory.category.infrastructure.dto.ResponsePayload;
import microservice.cloud.inventory.category.infrastructure.presentation.validate.CategoryAttributeDTO;
import microservice.cloud.inventory.category.infrastructure.presentation.validate.CategoryDTO;
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
        Slug slug = category.getSlug() == null? 
            Slug.create(category.getName()): 
            new Slug(category.getSlug());

        Category data = new Category(
            Id.generate(),
            category.getName(),
            slug,
            category.parent_id == null? null: new Id(category.getParent_id()),
            category.categoryAttributes == null? null: 
            category.categoryAttributes.stream().map(data -> {
                data.setId(Id.generate().value());
                data.setAttribute_definition_id(Id.generate().value());
                return toMap(data);
            }).toList() 
        );

        createCategoryUseCasePort.execute(
            data
        );

        CategoryDTO categoryDTO = toMap(data);

        return new ResponseEntity<>(
            ResponsePayload.<CategoryDTO>builder()
                .message("Category created successfully")
                .payload(categoryDTO)
                .build(),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponsePayload<CategoryDTO>> updateCategory(
        @PathVariable String id,
        @RequestBody CategoryDTO category
    ) {
        Category data = new Category(
            new Id(id),
            category.getName(),
            new Slug(category.getSlug()),
            category.parent_id == null? null: new Id(category.getParent_id()),
            category.categoryAttributes == null? null: category.categoryAttributes.stream().map(this::toMap).toList()
        );

        updateCategoryUseCasePort.execute(data);

        CategoryDTO categoryDTO = toMap(data);

        return new ResponseEntity<>(
            ResponsePayload.<CategoryDTO>builder()
                .message("Category updated successfully")
                .payload(categoryDTO)
                .build(),
                HttpStatus.CREATED
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(
        @RequestParam String id
    ) {
        deleteCategoryUseCasePort.execute(
            new Id(id)
        );

        return ResponseEntity.noContent().build();
    }

    private CategoryAttribute toMap(CategoryAttributeDTO categoryAttribute) {
        return new CategoryAttribute(
            Id.generate(),
            new AttributeDefinition(
                Id.generate(),
                categoryAttribute.getAttribute_definition_name(),
                new Slug(categoryAttribute.getAttribute_definition_slug()),
                DataType.valueOf(categoryAttribute.getAttribute_definition_type()),
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
                .attribute_definition_id(categoryAttribute.attribute_definition().id().value())
                .attribute_definition_name(categoryAttribute.attribute_definition().name())
                .attribute_definition_slug(categoryAttribute.attribute_definition().slug().value())
                .attribute_definition_type(categoryAttribute.attribute_definition().type().name())
                .attribute_definition_is_global(categoryAttribute.attribute_definition().is_global())
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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponsePayload<?>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        return new ResponseEntity<ResponsePayload<?>>(
            ResponsePayload.builder().errors(errors).message("Validation failed").build(), 
            HttpStatus.BAD_REQUEST
        );
    }

}
