package microservice.cloud.inventory.category.infrastructure.presentation.controller;

import java.util.Set;
import java.util.stream.Collectors;

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
import microservice.cloud.inventory.category.application.dtos.CategoryReadDTO;
import microservice.cloud.inventory.category.application.ports.in.CreateCategoryAttributeUseCasePort;
import microservice.cloud.inventory.category.application.ports.in.CreateCategoryUseCasePort;
import microservice.cloud.inventory.category.application.ports.in.DeleteCategoryAttributeUseCasePort;
import microservice.cloud.inventory.category.application.ports.in.DeleteCategoryUseCasePort;
import microservice.cloud.inventory.category.application.ports.in.ListCategoryUseCasePort;
import microservice.cloud.inventory.category.application.ports.in.UpdateCategoryUseCasePort;
import microservice.cloud.inventory.category.application.use_cases.ListCategoriesByIdsUseCase;
import microservice.cloud.inventory.category.domain.entity.Category;
import microservice.cloud.inventory.category.domain.entity.CategoryAttribute;
import microservice.cloud.inventory.category.domain.value_objects.Status;
import microservice.cloud.inventory.shared.infrastructure.dto.ResponsePayload;
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
    private final ListCategoriesByIdsUseCase listCategoriesByIdsUseCase;
    private final CreateCategoryUseCasePort createCategoryUseCasePort;
    private final UpdateCategoryUseCasePort updateCategoryUseCasePort;
    private final DeleteCategoryUseCasePort deleteCategoryUseCasePort;

    private final CreateCategoryAttributeUseCasePort createCategoryAttributeUseCasePort; 
    private final DeleteCategoryAttributeUseCasePort deleteCategoryAttributeUseCasePort; 

    @GetMapping
    public ResponseEntity<?> getCategories(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) Set<String> categoryIds
    ) {
        if(categoryIds == null) {
            if(page < 1 || size < 0) {
                page = 0;
                size = 10;
            }

            Pagination<CategoryReadDTO> categories = listCategoryUseCasePort.execute(page, size);
            
            return new ResponseEntity<>(
                categories,
                HttpStatus.OK
            );
        }

         return new ResponseEntity<>(
            listCategoriesByIdsUseCase.execute(categoryIds),
            HttpStatus.OK
        );
    }

    @PostMapping
    public ResponseEntity<ResponsePayload<CategoryDTO>> createCategory(
        @Valid @RequestBody CategoryDTO category
    ) {
        category.setId(Id.generate().value());
        Slug slug = Slug.fromString(category.getSlug());

        createCategoryUseCasePort.execute(
            new Category(
                Id.fromString(category.getId()),
                category.getName(),
                slug,
                category.getParent_id() == null? null: Id.fromString(category.getParent_id()),
                Status.ENABLED,
                category.getCategoryAttributes() == null
                    ? null
                    : category.getCategoryAttributes().stream().map(attr -> {
                        attr.setId(Id.generate().value());
                        return toMap(attr);
                    }).collect(Collectors.toSet())
            )
        );

        return new ResponseEntity<>(
            ResponsePayload.<CategoryDTO>builder()
                .message("Category created successfully")
                .payload(category)
                .build(),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{find_slug}")
    public ResponseEntity<ResponsePayload<UpdateCategoryDTO>> updateCategory(
        @PathVariable String find_slug,
        @Valid @RequestBody UpdateCategoryDTO category
    ) {
        Set<CategoryAttribute> attrs = category.categoryAttributes()
            .stream()
            .map(attr -> {
                return toMap(attr);
            })
            .collect(Collectors.toSet());

        updateCategoryUseCasePort.execute(
            Slug.fromString(find_slug),
            category.name(), 
            Slug.fromString(category.slug()), 
            category.parent_id() != null
                ? Id.fromString(category.parent_id())
                : null, 
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

    @DeleteMapping("/{find_slug}")
    public ResponseEntity<?> deleteCategory(
        @PathVariable String find_slug
    ) {
        deleteCategoryUseCasePort.execute(
            Slug.fromString(find_slug)
        );

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{find_slug}/attributes")
    public ResponseEntity<ResponsePayload<CategoryAttributeDTO>> createCategoryAttribute(
        @PathVariable String find_slug,
        @Valid @RequestBody CategoryAttributeDTO categoryAttribute
    ) {
        categoryAttribute.setId(Id.generate().value());

        createCategoryAttributeUseCasePort.execute(
            Slug.fromString(find_slug), 
            toMap(categoryAttribute)
        );

        return new ResponseEntity<>(
            ResponsePayload.<CategoryAttributeDTO>builder()
                .message("Category attribute added successfully")
                .payload(categoryAttribute)
                .build(),
                HttpStatus.CREATED
        );    
    }

    @DeleteMapping("/{find_slug}/attributes/{attr_id}")
    public ResponseEntity<?> deleteCategoryAttribute(
        @PathVariable String find_slug,
        @PathVariable String attr_id
    ) {
        deleteCategoryAttributeUseCasePort.execute(Slug.fromString(find_slug), Id.fromString(attr_id));

        return ResponseEntity.noContent().build();
    }

    private CategoryAttribute toMap(UpdateCategoryAttributeDTO attr) {
        CategoryAttribute catAttr = new CategoryAttribute(
            Id.fromString(attr.id()),
            Id.fromString(attr.attribute_definition_id()),
            attr.is_required(),
            attr.is_filterable(), 
            attr.is_sortable()
        );

        return catAttr;
    }

    private CategoryAttribute toMap(CategoryAttributeDTO attr) {
        CategoryAttribute catAttr = new CategoryAttribute(
            Id.fromString(attr.getId()),
            Id.fromString(attr.getAttribute_definition_id()),
            attr.getIs_required(),
            attr.getIs_filterable(),
            attr.getIs_sortable() 
        );

        return catAttr;
    }
}
