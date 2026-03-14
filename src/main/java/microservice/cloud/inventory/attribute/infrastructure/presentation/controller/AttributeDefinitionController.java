package microservice.cloud.inventory.attribute.infrastructure.presentation.controller;

import java.util.UUID;

import javax.smartcardio.ATR;

import org.springframework.data.repository.query.Param;
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
                new Id(id), 
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

    @PutMapping(name = "/{id}")
    public ResponseEntity<ResponsePayload<AttributeDefinitionDTO>> updateDefaultAttribute(
        @RequestParam String id,
        @Valid @RequestBody AttributeDefinitionDTO attribute
    ) {

        updateAttributeDefinitionUseCasePort.execute(
            new AttributeDefinition(
                new Id(id), 
                attribute.name(), 
                new Slug(attribute.slug()), 
                DataType.valueOf(attribute.type()), 
               attribute.is_global() 
            )
        );

        attribute = new AttributeDefinitionDTO(
            id, 
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

    @DeleteMapping(name = "/{id}")
    public ResponseEntity<?> deleteDefaultAttribute(
        @RequestParam String id
    ) {
        deleteAttributeDefinitionUseCasePort.execute(
            new Id(id)
        );

        return ResponseEntity.noContent().build();
    }
}
