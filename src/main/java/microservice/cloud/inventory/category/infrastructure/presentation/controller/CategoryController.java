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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import microservice.cloud.inventory.category.application.dtos.CategoryReadDTO;
import microservice.cloud.inventory.category.application.dtos.QueryCategories;
import microservice.cloud.inventory.category.application.use_cases.CreateCategoryAttributeUseCase;
import microservice.cloud.inventory.category.application.use_cases.CreateCategoryUseCase;
import microservice.cloud.inventory.category.application.use_cases.DeleteCategoryAttributeUseCase;
import microservice.cloud.inventory.category.application.use_cases.DeleteCategoryUseCase;
import microservice.cloud.inventory.category.application.use_cases.ListCategoryUseCase;
import microservice.cloud.inventory.category.application.use_cases.UpdateCategoryUseCase;
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
@Tag(name = "Categories", description = "Endpoints for managing inventory categories and category attributes")
public class CategoryController {

    private final ListCategoryUseCase listCategoryUseCase;
    private final ListCategoriesByIdsUseCase listCategoriesByIdsUseCase;
    private final CreateCategoryUseCase createCategoryUseCase;
    private final UpdateCategoryUseCase updateCategoryUseCase;
    private final DeleteCategoryUseCase deleteCategoryUseCase;

    private final CreateCategoryAttributeUseCase createCategoryAttributeUseCase; 
    private final DeleteCategoryAttributeUseCase deleteCategoryAttributeUseCase; 

    @Operation(summary = "Get categories", description = "Retrieves a paginated list of categories or fetches specific categories by IDs.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Categories retrieved successfully")
    })
    @GetMapping
    public ResponseEntity<?> getCategories(
        @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
        @Parameter(description = "Search filter by category name") @RequestParam(required = false) String search,
        @Parameter(description = "Set of category IDs to filter") @RequestParam(required = false) Set<String> categoryIds
    ) {
        if(categoryIds == null) {
            if(page < 1 || size < 0) {
                page = 0;
                size = 10;
            }

            Pagination<CategoryReadDTO> categories = listCategoryUseCase.execute(new QueryCategories(search), page, size);
            
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

    @Operation(summary = "Create category", description = "Creates a new category with optional category attributes.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Category created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid payload")
    })
    @PostMapping
    public ResponseEntity<ResponsePayload<CategoryDTO>> createCategory(
        @Valid @RequestBody CategoryDTO category
    ) {
        category.setId(Id.generate().value());
        Slug slug = Slug.fromString(category.getSlug());

        createCategoryUseCase.execute(
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

    @Operation(summary = "Update category", description = "Updates an existing category by slug.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Category updated successfully"),
        @ApiResponse(responseCode = "404", description = "Category not found")
    })
    @PutMapping("/{find_slug}")
    public ResponseEntity<ResponsePayload<UpdateCategoryDTO>> updateCategory(
        @Parameter(description = "Category slug") @PathVariable String find_slug,
        @Valid @RequestBody UpdateCategoryDTO category
    ) {
        Set<CategoryAttribute> attrs = category.categoryAttributes()
            .stream()
            .map(attr -> {
                return toMap(attr);
            })
            .collect(Collectors.toSet());

        updateCategoryUseCase.execute(
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

    @Operation(summary = "Delete category", description = "Deletes a category by slug.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Category deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Category not found")
    })
    @DeleteMapping("/{find_slug}")
    public ResponseEntity<?> deleteCategory(
        @Parameter(description = "Category slug") @PathVariable String find_slug
    ) {
        deleteCategoryUseCase.execute(
            Slug.fromString(find_slug)
        );

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Create category attribute", description = "Adds an attribute binding to a category by category slug.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Category attribute added successfully"),
        @ApiResponse(responseCode = "404", description = "Category not found")
    })
    @PostMapping("/{find_slug}/attributes")
    public ResponseEntity<ResponsePayload<CategoryAttributeDTO>> createCategoryAttribute(
        @Parameter(description = "Category slug") @PathVariable String find_slug,
        @Valid @RequestBody CategoryAttributeDTO categoryAttribute
    ) {
        categoryAttribute.setId(Id.generate().value());

        createCategoryAttributeUseCase.execute(
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

    @Operation(summary = "Delete category attribute", description = "Removes an attribute binding from a category.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Category attribute deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Category or attribute not found")
    })
    @DeleteMapping("/{find_slug}/attributes/{attr_id}")
    public ResponseEntity<?> deleteCategoryAttribute(
        @Parameter(description = "Category slug") @PathVariable String find_slug,
        @Parameter(description = "Category Attribute ID") @PathVariable String attr_id
    ) {
        deleteCategoryAttributeUseCase.execute(Slug.fromString(find_slug), Id.fromString(attr_id));

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
