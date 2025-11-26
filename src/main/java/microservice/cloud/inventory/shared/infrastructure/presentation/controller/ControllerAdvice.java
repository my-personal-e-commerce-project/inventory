package microservice.cloud.inventory.shared.infrastructure.presentation.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;

import microservice.cloud.inventory.category.infrastructure.dto.ResponsePayload;
import microservice.cloud.inventory.shared.domain.exception.DataNotFound;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(DataNotFound.class)
    public ResponseEntity<ResponsePayload<?>> handleDataNotFound(
            DataNotFound ex) {
        
        return new ResponseEntity<ResponsePayload<?>>(
            ResponsePayload.builder().message(ex.getMessage()).build(), 
            HttpStatus.NOT_FOUND
        );
    }

    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, Object> response = new HashMap<>();

        List<Map<String, String>> errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(err -> Map.of(
                "field", err.getField(),
                "message", err.getDefaultMessage()
            ))
            .toList();

        response.put("status", 400);
        response.put("errors", errors);
        return response;
    }


    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ResponsePayload<?>> handleRuntimeException(
            RuntimeException ex) {

        return new ResponseEntity<ResponsePayload<?>>(
            ResponsePayload.builder().message(ex.getMessage()).build(), 
            HttpStatus.BAD_REQUEST
        );
    }


    @ExceptionHandler(JpaObjectRetrievalFailureException.class)
    public ResponseEntity<ResponsePayload<?>> handleEntityNotFoundException(
            JpaObjectRetrievalFailureException ex) {

        return new ResponseEntity<ResponsePayload<?>>(
            ResponsePayload.builder().message(ex.getMessage()).build(), 
            HttpStatus.NOT_FOUND
        );
    }
}
