package microservice.cloud.inventory.product.infrastructure.presentation.controller;

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
import microservice.cloud.inventory.product.application.ports.in.AddProductAttributeUseCasePort;
import microservice.cloud.inventory.product.application.ports.in.CreateProductUseCasePort;
import microservice.cloud.inventory.product.application.ports.in.DeleteProductAttributeUseCasePort;
import microservice.cloud.inventory.product.application.ports.in.DeleteProductUseCasePort;
import microservice.cloud.inventory.product.application.ports.in.ListProductsUseCasePort;
import microservice.cloud.inventory.product.application.ports.in.UpdateProductUseCasePort;
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
    
    private final CreateProductUseCasePort createProductUseCasePort;
    private final UpdateProductUseCasePort updateProductUseCasePort;
    private final ListProductsUseCasePort listProductsUseCasePort;
    private final DeleteProductUseCasePort deleteProductUseCasePort;
    private final AddProductAttributeUseCasePort addProductAttributeUseCasePort;
    private final DeleteProductAttributeUseCasePort deleteProductAttributeUseCasePort;

    @GetMapping
    public ResponseEntity<?> listProducts(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        Pagination<ProductReadDTO> products = listProductsUseCasePort.execute(page, size);

        return ResponseEntity.ok(
                products
        );
    }
    
    @PostMapping
    public ResponseEntity<ResponsePayload<ProductDTO>> createProduct(
        @Valid @RequestBody ProductDTO productDTO
    ) {
        productDTO.setId(Id.generate().value());
        productDTO.setAttributes(
            productDTO.getAttributes()
                .stream()
                .map(i -> {
                    i.setId(Id.generate().value());
                    return i;
                })
                .toList()
        );

        createProductUseCasePort.execute(
            productDTO.getTitle(),
            Slug.fromString(productDTO.getSlug()),
            productDTO.getDescription(),
            productDTO.getCategories(),
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
            productDTO.getImages(),
            productDTO.getTags()
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

        updateProductUseCasePort.execute(
            Slug.fromString(find_slug),
            productDTO.getTitle(),
            Slug.fromString(productDTO.getSlug()),
            productDTO.getDescription(),
            productDTO.getCategories(),
            new Price(productDTO.getPrice()),
            new Quantity(productDTO.getStock()),
            productDTO.getImages(),
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
            productDTO.getTags()
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
        deleteProductUseCasePort.execute(Slug.fromString(find_slug));
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

        addProductAttributeUseCasePort.execute(Slug.fromString(find_slug), productAttributeValue);

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
        deleteProductAttributeUseCasePort.execute(Slug.fromString(find_slug), Id.fromString(attr_id));

        return ResponseEntity.noContent().build();
    }
}
