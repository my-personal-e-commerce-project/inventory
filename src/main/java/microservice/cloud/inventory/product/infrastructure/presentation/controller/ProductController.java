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
import microservice.cloud.inventory.shared.application.dto.Pagination;
import microservice.cloud.inventory.shared.domain.value_objects.Id;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {
    
    private final CreateProductUseCase createProductUseCase;
    private final UpdateProductUseCase updateProductUseCase;
    private final ListProductsUseCase listProductsUseCase;
    private final DeleteProductUseCase deleteProductUseCase;
    private final AddProductAttributeUseCase addProductAttributeUseCase;
    private final DeleteProductAttributeUseCase deleteProductAttributeUseCase;

    @GetMapping
    public ResponseEntity<?> listProducts(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String search,
        @RequestParam(required = false) Set<String> categories,
        @RequestParam(required = false) Double minPrice,
        @RequestParam(required = false) Double maxPrice,
        @RequestParam(required = false) Integer minStock,
        @RequestParam(required = false) Integer maxStock,
        @RequestParam(required = false) Boolean isActive
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

        return ResponseEntity.ok(
            products
        );
    }
    
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
                new Quantity(productDTO.getStock()),
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

    @PutMapping("/{find_slug}")
    public ResponseEntity<ResponsePayload<UpdateProductDTO>> updateProduct(
        @PathVariable String find_slug,
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
            new Quantity(productDTO.stock()),
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

    @DeleteMapping("/{find_slug}")
    public ResponseEntity<?> deleteProduct(
        @PathVariable String find_slug
    ) {
        deleteProductUseCase.execute(Slug.fromString(find_slug));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{find_slug}/attributes")
    public ResponseEntity<ResponsePayload<ProductAttributeValueDTO>> addAttributeProduct(
        @PathVariable String find_slug,
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

    @DeleteMapping("/{find_slug}/attributes/{attr_id}")
    public ResponseEntity<ResponsePayload<ProductAttributeValueDTO>> removeAttributeProduct(
        @PathVariable String find_slug,
        @PathVariable String attr_id
    ) {
        deleteProductAttributeUseCase.execute(Slug.fromString(find_slug), Id.fromString(attr_id));

        return ResponseEntity.noContent().build();
    }
}
