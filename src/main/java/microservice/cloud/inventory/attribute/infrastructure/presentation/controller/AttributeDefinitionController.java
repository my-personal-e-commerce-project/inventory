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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import microservice.cloud.inventory.attribute.application.ports.dto.QueryAttributeDefinitions;
import microservice.cloud.inventory.attribute.application.use_cases.CreateAttributeDefinitionUseCase;
import microservice.cloud.inventory.attribute.application.use_cases.DeleteAttributeDefinitionUseCase;
import microservice.cloud.inventory.attribute.application.use_cases.ListAttributeDefinitionUseCase;
import microservice.cloud.inventory.attribute.application.use_cases.UpdateAttributeDefinitionUseCase;
import microservice.cloud.inventory.attribute.domain.entity.AttributeDefinition;
import microservice.cloud.inventory.attribute.infrastructure.presentation.validate.AttributeDefinitionDTO;
import microservice.cloud.inventory.attribute.infrastructure.presentation.validate.UpdateAttributeDefinitionDTO;
import microservice.cloud.inventory.shared.domain.value_objects.Id;
import microservice.cloud.inventory.shared.domain.value_objects.Slug;
import microservice.cloud.inventory.shared.infrastructure.dto.ResponsePayload;
import microservice.cloud.inventory.attribute.domain.value_objects.DataType;

@RestController
@RequestMapping("/api/v1/attribute_definitions")
@RequiredArgsConstructor
@Tag(name = "Attribute Definitions", description = "Endpoints for managing global and category product attribute definitions")
public class AttributeDefinitionController {

    private final CreateAttributeDefinitionUseCase createAttributeDefinitionUseCase;
    private final UpdateAttributeDefinitionUseCase updateAttributeDefinitionUseCase;
    private final DeleteAttributeDefinitionUseCase deleteAttributeDefinitionUseCase;
    private final ListAttributeDefinitionUseCase listAttributeDefinitionUseCase;

    @Operation(summary = "List attribute definitions", description = "Retrieves a paginated list of attribute definitions with search filter.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Attribute definitions retrieved successfully")
    })
    @GetMapping
    public ResponseEntity<?> listDefaultAttributes(
        @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
        @Parameter(description = "Search filter by name") @RequestParam(required = false) String search
    ) {
        return ResponseEntity.ok(
            listAttributeDefinitionUseCase.execute(new QueryAttributeDefinitions(search), page, size)
        );
    }

    @Operation(summary = "Create attribute definition", description = "Creates a new global or category-level attribute definition.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Attribute definition created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid payload")
    })
    @PostMapping
    public ResponseEntity<ResponsePayload<AttributeDefinitionDTO>> createDefaultAttribute(
        @Valid @RequestBody AttributeDefinitionDTO attribute
    ) {
        String id = UUID.randomUUID().toString();

        Slug slug = Slug.create(attribute.slug());

        createAttributeDefinitionUseCase.execute(
            Id.fromString(id), 
            attribute.name(),
            slug,
            DataType.valueOf(attribute.type()), 
            attribute.is_global()
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

    @Operation(summary = "Update attribute definition", description = "Updates an existing attribute definition by slug.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Attribute definition updated successfully"),
        @ApiResponse(responseCode = "404", description = "Attribute definition not found")
    })
    @PutMapping(name = "/{find_slug}")
    public ResponseEntity<ResponsePayload<UpdateAttributeDefinitionDTO>> updateAttributeDefinition(
        @Parameter(description = "Attribute definition slug") @RequestParam String find_slug,
        @Valid @RequestBody UpdateAttributeDefinitionDTO attribute
    ) {
        AttributeDefinition attrDef = updateAttributeDefinitionUseCase.execute(
            Slug.fromString(find_slug),
            attribute.name(), 
            Slug.fromString(attribute.slug()), 
            DataType.valueOf(attribute.type()), 
            attribute.is_global() 
        );

        attribute = new UpdateAttributeDefinitionDTO(
            attrDef.id().value(),
            attrDef.name(), 
            attrDef.slug().value(), 
            attrDef.type().toString(), 
            attrDef.is_global()
        );

        return ResponseEntity.ok(
                ResponsePayload.<UpdateAttributeDefinitionDTO>builder()
                    .payload(attribute).build()
            );
    }

    @Operation(summary = "Delete attribute definition", description = "Deletes an attribute definition by slug.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Attribute definition deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Attribute definition not found")
    })
    @DeleteMapping("/{find_slug}")
    public ResponseEntity<?> deleteAttributeDefinition(
        @Parameter(description = "Attribute definition slug") @PathVariable String find_slug
    ) {
        deleteAttributeDefinitionUseCase.execute(
            Slug.fromString(find_slug)
        );

        return ResponseEntity.noContent().build();
    }
}
