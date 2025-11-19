package microservice.cloud.inventory.category.infrastructure.presentation.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import microservice.cloud.inventory.category.application.ports.in.ListAttributesUseCasePort;
import microservice.cloud.inventory.category.domain.entity.CategoryAttribute;
import microservice.cloud.inventory.category.infrastructure.dto.ResponsePayload;
import microservice.cloud.inventory.shared.application.dto.Pagination;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final ListAttributesUseCasePort listAttributesUseCasePort;

    @GetMapping("/attributes")
    public ResponseEntity<ResponsePayload<Pagination<CategoryAttribute>>> getAttributes(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        Pagination<CategoryAttribute> attributes = listAttributesUseCasePort.execute(0, 10);

        return new ResponseEntity<>(
            ResponsePayload.<Pagination<CategoryAttribute>>builder()
                .message("Attributes retrieved successfully")
                .payload(attributes)
                .build(),
                HttpStatus.OK
        );
    }
}
