package microservice.cloud.inventory.category.infrastructure.presentation.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import microservice.cloud.inventory.category.application.ports.in.CreateCategoryUseCasePort;
import microservice.cloud.inventory.category.application.ports.in.DeleteCategoryUseCasePort;
import microservice.cloud.inventory.category.application.ports.in.ListCategoryUseCasePort;
import microservice.cloud.inventory.category.application.ports.in.UpdateCategoryUseCasePort;
import microservice.cloud.inventory.category.domain.entity.Category;
import microservice.cloud.inventory.category.infrastructure.dto.ResponsePayload;
import microservice.cloud.inventory.category.infrastructure.presentation.validate.CategoryAttributeDTO;
import microservice.cloud.inventory.category.infrastructure.presentation.validate.CategoryDTO;
import microservice.cloud.inventory.shared.application.dto.Pagination;
import microservice.cloud.inventory.shared.domain.value_objects.Id;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final ListCategoryUseCasePort listCategoryUseCasePort;
    private final CreateCategoryUseCasePort createCategoryUseCasePort;
    private final UpdateCategoryUseCasePort updateCategoryUseCasePort;
    private final DeleteCategoryUseCasePort deleteCategoryUseCasePort;

    @GetMapping
    public ResponseEntity<ResponsePayload<Pagination<Category>>> getAttributes(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        Pagination<Category> categories = listCategoryUseCasePort.execute(0, 10);

        return new ResponseEntity<>(
            ResponsePayload.<Pagination<Category>>builder()
                .message("Attributes retrieved successfully")
                .payload(categories)
                .build(),
                HttpStatus.OK
        );
    }

    @PostMapping
    public ResponseEntity<ResponsePayload<Category>> createCategory(
        @RequestBody CategoryDTO category
    ) {
        Slug slug = category.getSlug() == null? 
            Slug.create(category.getName()): 
            new Slug(category.getSlug());

        Category data = new Category(
            Id.generate(),
            category.getName(),
            slug,
            category.parent_id == null? null: new Id(category.getParent_id()),
            null
        );

        createCategoryUseCasePort.execute(
            data
        );

        return new ResponseEntity<>(
            ResponsePayload.<Category>builder()
                .message("Category created successfully")
                .payload(data)
                .build(),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponsePayload<Category>> updateCategory(
        @RequestParam String id,
        @RequestBody CategoryDTO category
    ) {
        Category data = new Category(
            new Id(id),
            category.getName(),
            new Slug(category.getSlug()),
            category.parent_id == null? null: new Id(category.getParent_id()),
            null
        );

        updateCategoryUseCasePort.execute(
            data
        );

        return new ResponseEntity<>(
            ResponsePayload.<Category>builder()
                .message("Category updated successfully")
                .payload(data)
                .build(),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{id}/attributes")
    public ResponseEntity<ResponsePayload<Category>> addCategoryAttribute(
        @RequestParam String id,
        @RequestBody CategoryAttributeDTO categoryAttribute
    ) {
        return null;
    }

    @PutMapping("/{id}/attributes/{id}")
    public ResponseEntity<ResponsePayload<Category>> updateCategoryAttribute() {
        return null;
    }

    @PutMapping("/{id}/attributes/{id}")
    public ResponseEntity<ResponsePayload<Category>> removeCategoryAttribute() {
        return null;
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
}
