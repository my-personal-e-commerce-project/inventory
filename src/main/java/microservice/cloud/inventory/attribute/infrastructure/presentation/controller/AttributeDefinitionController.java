package microservice.cloud.inventory.attribute.infrastructure.presentation.controller;

import java.util.UUID;

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
import microservice.cloud.inventory.attribute.application.ports.in.CreateAttributeDefinitionUseCasePort;
import microservice.cloud.inventory.attribute.application.ports.in.DeleteAttributeDefinitionUseCasePort;
import microservice.cloud.inventory.attribute.application.ports.in.ListAttributeDefinitionUseCasePort;
import microservice.cloud.inventory.attribute.application.ports.in.UpdateAttributeDefinitionUseCasePort;
import microservice.cloud.inventory.attribute.domain.entity.AttributeDefinition;
import microservice.cloud.inventory.attribute.infrastructure.presentation.validate.AttributeDefinitionDTO;
import microservice.cloud.inventory.shared.domain.value_objects.Id;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;
import microservice.cloud.inventory.shared.infrastructure.dto.ResponsePayload;
import microservice.cloud.inventory.attribute.domain.value_objects.DataType;

@RestController
@RequestMapping("/api/v1/attribute_definitions")
@RequiredArgsConstructor
public class AttributeDefinitionController {

    private final CreateAttributeDefinitionUseCasePort createAttributeDefinitionUseCasePort;
    private final UpdateAttributeDefinitionUseCasePort updateAttributeDefinitionUseCasePort;
    private final DeleteAttributeDefinitionUseCasePort deleteAttributeDefinitionUseCasePort;
    private final ListAttributeDefinitionUseCasePort listAttributeDefinitionUseCasePort;

    @GetMapping
    public ResponseEntity<?> listDefaultAttributes(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(
            listAttributeDefinitionUseCasePort.execute(page, size)
        );
    }

    @PostMapping
    public ResponseEntity<ResponsePayload<AttributeDefinitionDTO>> createDefaultAttribute(
        @Valid @RequestBody AttributeDefinitionDTO attribute
    ) {
        String id = UUID.randomUUID().toString();

        Slug slug = Slug.create(attribute.slug());

        createAttributeDefinitionUseCasePort.execute(
            new AttributeDefinition(
                Id.fromString(id), 
                attribute.name(),
                slug,
                DataType.valueOf(attribute.type()), 
                attribute.is_global() 
            )
        );

        attribute = new AttributeDefinitionDTO(
            id, 
            attribute.name(), 
            slug.value(), 
            attribute.type(), 
            attribute.is_global()
        );
        
        return new ResponseEntity<ResponsePayload<AttributeDefinitionDTO>>(
                ResponsePayload.<AttributeDefinitionDTO>builder()
                    .payload(attribute).build(), 
                HttpStatus.CREATED
            );
    }

    @PutMapping(name = "/{find_slug}")
    public ResponseEntity<ResponsePayload<AttributeDefinitionDTO>> updateDefaultAttribute(
        @RequestParam String find_slug,
        @Valid @RequestBody AttributeDefinitionDTO attribute
    ) {
        updateAttributeDefinitionUseCasePort.execute(
            Slug.fromString(find_slug),
            attribute.name(), 
            Slug.fromString(attribute.slug()), 
            DataType.valueOf(attribute.type()), 
            attribute.is_global() 
        );

        attribute = new AttributeDefinitionDTO(
            attribute.name(), 
            attribute.slug(), 
            attribute.type(), 
            attribute.is_global()
        );

        return ResponseEntity.ok(
                ResponsePayload.<AttributeDefinitionDTO>builder()
                    .payload(attribute).build()
            );
    }

    @DeleteMapping("/{find_slug}")
    public ResponseEntity<?> deleteDefaultAttribute(
        @PathVariable String find_slug
    ) {
        deleteAttributeDefinitionUseCasePort.execute(
            Slug.fromString(find_slug)
        );

        return ResponseEntity.noContent().build();
    }
}
