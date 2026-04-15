package qm.service.book.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void handleConstraintViolation_ShouldReturnBadRequest() {
        // Given
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        when(violation.getMessage()).thenReturn("ISBN must not be null");
        ConstraintViolationException ex = new ConstraintViolationException("Validation failed", Set.of(violation));

        // When
        ResponseEntity<ErrorResponse> response = handler.handleConstraintViolation(ex);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().errorCode()).isEqualTo("VALIDATION_FAILED");
        assertThat(response.getBody().message()).isEqualTo("ISBN must not be null");
    }

    @Test
    void handleNotFound_ShouldReturnNotFound() {
        // Given
        ResourceNotFoundException ex = new ResourceNotFoundException("Book not found");

        // When
        ResponseEntity<ErrorResponse> response = handler.handleNotFound(ex);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().errorCode()).isEqualTo("RESOURCE_NOT_FOUND");
        assertThat(response.getBody().message()).isEqualTo("Book not found");
    }

    @Test
    @SuppressWarnings("unchecked")
    void handleValidation_ShouldReturnBadRequestWithFieldErrors() {
        // Given
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("book", "title", "Title is required");
        
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        // When
        ResponseEntity<Map<String, Object>> response = handler.handleValidation(ex);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsEntry("errorCode", "VALIDATION_FAILED");
        Map<String, String> errors = (Map<String, String>) response.getBody().get("errors");
        assertThat(response.getBody()).containsKey("timestamp");
        assertThat(errors).containsEntry("title", "Title is required");
    }

    @Test
    void handleConflict_ShouldReturnConflict() {
        // Given
        IllegalArgumentException ex = new IllegalArgumentException("Invalid state");

        // When
        ResponseEntity<ErrorResponse> response = handler.handleConflict(ex);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().errorCode()).isEqualTo("CONFLICT");
    }
}