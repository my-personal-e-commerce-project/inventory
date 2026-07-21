package microservice.cloud.inventory.product.infrastructure.presentation.controller;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;
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
import microservice.cloud.inventory.shared.infrastructure.dto.ResponsePayload;
import microservice.cloud.inventory.product.application.dtos.ProductReadDTO;
import microservice.cloud.inventory.product.application.dtos.QueryProducts;
import microservice.cloud.inventory.product.application.use_cases.AddProductAttributeUseCase;
import microservice.cloud.inventory.product.application.use_cases.CreateProductUseCase;
import microservice.cloud.inventory.product.application.use_cases.DeleteProductAttributeUseCase;
import microservice.cloud.inventory.product.application.use_cases.DeleteProductUseCase;
import microservice.cloud.inventory.product.application.use_cases.ListProductsUseCase;
import microservice.cloud.inventory.product.application.use_cases.UpdateProductUseCase;
import microservice.cloud.inventory.product.domain.entity.Product;
import microservice.cloud.inventory.product.domain.entity.ProductAttributeValue;
import microservice.cloud.inventory.product.domain.value_objects.Price;
import microservice.cloud.inventory.product.domain.value_objects.Quantity;
import microservice.cloud.inventory.product.infrastructure.presentation.validate.ProductAttributeValueDTO;
import microservice.cloud.inventory.product.infrastructure.presentation.validate.ProductDTO;
import microservice.cloud.inventory.product.infrastructure.presentation.validate.UpdateProductDTO;
import microservice.cloud.inventory.productStock.application.use_cases.CreateProductStockUseCase;
import microservice.cloud.inventory.productStock.application.use_cases.UpdateProductStockUseCase;
import microservice.cloud.inventory.productStock.domain.entity.ProductStock;
import microservice.cloud.inventory.shared.application.dto.Pagination;
import microservice.cloud.inventory.shared.domain.value_objects.Id;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Products & Stock", description = "Endpoints for managing products, attributes, and stock mutations with pessimistic locking")
public class ProductController {
    
    private final CreateProductUseCase createProductUseCase;
    private final UpdateProductUseCase updateProductUseCase;
    private final ListProductsUseCase listProductsUseCase;
    private final DeleteProductUseCase deleteProductUseCase;
    private final AddProductAttributeUseCase addProductAttributeUseCase;
    private final DeleteProductAttributeUseCase deleteProductAttributeUseCase;

    private final CreateProductStockUseCase createProductStockUseCase;
    private final UpdateProductStockUseCase updateProductStockUseCase;

    @Operation(summary = "List products", description = "Retrieves a paginated list of products with optional filtering parameters.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Products retrieved successfully")
    })
    @GetMapping
    public ResponseEntity<?> listProducts(
        @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
        @Parameter(description = "Search term by title or description") @RequestParam(required = false) String search,
        @Parameter(description = "Set of category IDs to filter") @RequestParam(required = false) Set<String> categories,
        @Parameter(description = "Minimum price filter") @RequestParam(required = false) Double minPrice,
        @Parameter(description = "Maximum price filter") @RequestParam(required = false) Double maxPrice,
        @Parameter(description = "Minimum stock filter") @RequestParam(required = false) Integer minStock,
        @Parameter(description = "Maximum stock filter") @RequestParam(required = false) Integer maxStock,
        @Parameter(description = "Active status filter") @RequestParam(required = false) Boolean isActive
    ) {
        Pagination<ProductReadDTO> products = listProductsUseCase.execute(page, size, 
            new QueryProducts(
                search,
                (categories == null || categories.isEmpty()) ? null : new ArrayList<>(categories),
                minPrice,
                maxPrice,
                minStock,
                maxStock,
                isActive
            )
        );

        return ResponseEntity.ok(products);
    }
    
    @Operation(summary = "Create product", description = "Creates a new product and initializes its stock record.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Product and stock created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid payload or validation error"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping
    public ResponseEntity<ResponsePayload<ProductDTO>> createProduct(
        @Valid @RequestBody ProductDTO productDTO
    ) { 
        productDTO.setAttributes(
            productDTO.getAttributes()
                .stream()
                .map(i -> {
                    i.setId(Id.generate().value());
                    return i;
                })
                .toList()
        );

        productDTO.setId(Id.generate().value());
        productDTO.setStockId(Id.generate().value());

        createProductStockUseCase.execute(
            new ProductStock(
                Id.fromString(productDTO.getStockId()),
                new Quantity(productDTO.getStock())
            )
        );

        createProductUseCase.execute(
            new Product(
                Id.fromString(productDTO.getId()),
                productDTO.getTitle(),
                Slug.fromString(productDTO.getSlug()),
                productDTO.getDescription(),
                productDTO.getCategories(),
                productDTO.isActive(),
                new Price(productDTO.getPrice()),
                productDTO.getAttributes().stream().map(attr -> 
                    new ProductAttributeValue(
                        Id.fromString(attr.getId()),
                        Id.fromString(attr.getAttribute_definition_id()),
                        attr.getString_value(),
                        attr.getInteger_value(),
                        attr.getDouble_value(),
                        attr.getBoolean_value()
                    )
                ).collect(Collectors.toSet()),
                Id.fromString(productDTO.getStockId()),
                productDTO.getMinStock() == null? null: new Quantity(productDTO.getMinStock()),
                null,
                productDTO.getTags()
            )
        );

        return new ResponseEntity<>(
            ResponsePayload.<ProductDTO>builder().payload(productDTO).build(),
            HttpStatus.CREATED
        );
    }

    @Operation(summary = "Update product", description = "Updates product details by product slug.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product updated successfully"),
        @ApiResponse(responseCode = "404", description = "Product not found"),
        @ApiResponse(responseCode = "400", description = "Invalid payload")
    })
    @PutMapping("/{find_slug}")
    public ResponseEntity<ResponsePayload<UpdateProductDTO>> updateProduct(
        @Parameter(description = "Product slug") @PathVariable String find_slug,
        @Valid @RequestBody UpdateProductDTO productDTO
    ) {
        updateProductUseCase.execute(
            Slug.fromString(find_slug),
            productDTO.title(),
            Slug.fromString(productDTO.slug()),
            productDTO.description(),
            productDTO.categories(),
            productDTO.isActive(),
            new Price(productDTO.price()),
            new Quantity(productDTO.minStock()),
            null,
            productDTO.attributes().stream().map(attr -> 
                new ProductAttributeValue(
                    Id.fromString(attr.id()),
                    Id.fromString(attr.attribute_definition_id()),
                    attr.string_value(),
                    attr.integer_value(),
                    attr.double_value(),
                    attr.boolean_value()
                )
            ).collect(Collectors.toSet()),
            productDTO.tags()
        );

        return new ResponseEntity<>(
            ResponsePayload.<UpdateProductDTO>builder().payload(productDTO).build(),
            HttpStatus.OK
        );
    }

    @Operation(summary = "Update product stock (Pessimistic Locking)", description = "Mutates product stock quantity using pessimistic locking and triggers min stock alert event if threshold reached.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Stock updated successfully"),
        @ApiResponse(responseCode = "404", description = "Product not found"),
        @ApiResponse(responseCode = "400", description = "Invalid stock quantity")
    })
    @PutMapping("/{find_slug}/stock")
    public ResponseEntity<ResponsePayload<Integer>> updateProductStock(
        @Parameter(description = "Product slug") @PathVariable String find_slug,
        @Valid @RequestBody Integer stock
    ) {
        updateProductStockUseCase.execute(Slug.fromString(find_slug), new Quantity(stock));

        return new ResponseEntity<>(
            ResponsePayload.<Integer>builder().payload(stock).build(),
            HttpStatus.OK
        );
    }

    @Operation(summary = "Delete product", description = "Deletes a product by slug.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Product deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @DeleteMapping("/{find_slug}")
    public ResponseEntity<?> deleteProduct(
        @Parameter(description = "Product slug") @PathVariable String find_slug
    ) {
        deleteProductUseCase.execute(Slug.fromString(find_slug));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Add attribute to product", description = "Adds a new attribute value to an existing product.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Attribute added successfully"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @PostMapping("/{find_slug}/attributes")
    public ResponseEntity<ResponsePayload<ProductAttributeValueDTO>> addAttributeProduct(
        @Parameter(description = "Product slug") @PathVariable String find_slug,
        @Valid @RequestBody ProductAttributeValueDTO attr
    ) {
        attr.setId(UUID.randomUUID().toString());
        ProductAttributeValue productAttributeValue = new ProductAttributeValue(
            Id.fromString(attr.getId()),
            Id.fromString(attr.getAttribute_definition_id()),
            attr.getString_value(),
            attr.getInteger_value(),
            attr.getDouble_value(),
            attr.getBoolean_value()
        );

        addProductAttributeUseCase.execute(Slug.fromString(find_slug), productAttributeValue);

        return new ResponseEntity<>(
            ResponsePayload.<ProductAttributeValueDTO>builder().payload(attr).build(),
            HttpStatus.OK
        );
    }

    @Operation(summary = "Remove attribute from product", description = "Removes an attribute value from a product by attribute ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Attribute removed successfully"),
        @ApiResponse(responseCode = "404", description = "Product or attribute not found")
    })
    @DeleteMapping("/{find_slug}/attributes/{attr_id}")
    public ResponseEntity<ResponsePayload<ProductAttributeValueDTO>> removeAttributeProduct(
        @Parameter(description = "Product slug") @PathVariable String find_slug,
        @Parameter(description = "Attribute ID") @PathVariable String attr_id
    ) {
        deleteProductAttributeUseCase.execute(Slug.fromString(find_slug), Id.fromString(attr_id));

        return ResponseEntity.noContent().build();
    }
}
