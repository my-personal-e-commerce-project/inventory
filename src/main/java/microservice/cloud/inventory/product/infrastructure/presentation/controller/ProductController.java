package microservice.cloud.inventory.product.infrastructure.presentation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import microservice.cloud.inventory.category.infrastructure.dto.ResponsePayload;
import microservice.cloud.inventory.product.application.dtos.ProductReadDTO;
import microservice.cloud.inventory.product.application.ports.in.CreateProductUseCasePort;
import microservice.cloud.inventory.product.application.ports.in.ListProductsUseCasePort;
import microservice.cloud.inventory.product.domain.entity.Product;
import microservice.cloud.inventory.product.domain.entity.ProductAttributeValue;
import microservice.cloud.inventory.product.domain.value_objects.Price;
import microservice.cloud.inventory.product.domain.value_objects.Quantity;
import microservice.cloud.inventory.product.infrastructure.presentation.validate.ProductDTO;
import microservice.cloud.inventory.shared.application.dto.Pagination;
import microservice.cloud.inventory.shared.domain.value_objects.Id;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {
    
    private final CreateProductUseCasePort createProductUseCasePort;
    private final ListProductsUseCasePort listProductsUseCasePort;

    @GetMapping
    public ResponseEntity<?> listProducts(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        Pagination<ProductReadDTO> products = listProductsUseCasePort.execute(page, size);

        return ResponseEntity.ok(
            ResponsePayload.builder().payload(products).build()
        );
    }
    
    @PostMapping
    public ResponseEntity<ResponsePayload<ProductDTO>> createProduct(
        @Valid @RequestBody ProductDTO productDTO
    ) {

        Product product = new Product(
            Id.generate(),
            productDTO.getTitle(),
            new Slug(productDTO.getSlug()),
            productDTO.getDescription(),
            productDTO.getCategories(),
            new Price(productDTO.getPrice()),
            productDTO.getAttributes().stream().map(attr -> 
                new ProductAttributeValue(
                    Id.generate(),
                    new Id(attr.getAttribute_definition_id()),
                    attr.getString_value(),
                    attr.getInteger_value(),
                    attr.getDouble_value(),
                    attr.getBoolean_value()
                )
            ).toList(),
            new Quantity(productDTO.getStock()),
            productDTO.getImages()
        );

        createProductUseCasePort.execute(product);

        return ResponseEntity.ok(
            ResponsePayload.<ProductDTO>builder().payload(productDTO).build()
        );
    }
}
