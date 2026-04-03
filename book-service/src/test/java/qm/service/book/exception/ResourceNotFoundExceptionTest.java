package qm.service.book.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ResourceNotFoundExceptionTest -
 */
class ResourceNotFoundExceptionTest {

    @Test
    void shouldCreateExceptionWithMessage() {
        // Given
        String message = "Custom error message";

        // When
        ResourceNotFoundException exception = new ResourceNotFoundException(message);

        // Then
        assertThat(exception.getMessage()).isEqualTo(message);
    }

    @Test
    void shouldCreateFormattedMessage() {
        // Given
        String resource = "Book";
        String field = "id";
        Long value = 101L;

        // When
        ResourceNotFoundException exception = new ResourceNotFoundException(resource, field, value);

        // Then
        String expectedMessage = "Book not found with id : '101'";
        assertThat(exception.getMessage()).isEqualTo(expectedMessage);
    }

    @Test
    void shouldHaveNotFoundStatusAnnotation() {
        ResponseStatus annotation = ResourceNotFoundException.class.getAnnotation(ResponseStatus.class);

        assertThat(annotation).isNotNull();
        assertThat(annotation.value()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
