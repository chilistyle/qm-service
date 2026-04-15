package qm.service.book.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class BookRequestDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldPassValidation_WithValidData() {
        BookRequestDTO dto = new BookRequestDTO(
                "The Great Gatsby",
                "F. Scott Fitzgerald",
                "9780743273565",
                new BigDecimal("15.99")
        );

        Set<ConstraintViolation<BookRequestDTO>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    @Test
    void shouldFailValidation_WhenTitleIsBlank() {
        BookRequestDTO dto = new BookRequestDTO(" ", "Author", "9780743273565", BigDecimal.TEN);

        Set<ConstraintViolation<BookRequestDTO>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Title is required");
    }

    @Test
    void shouldFailValidation_WhenTitleIsTooLong() {
        String longTitle = "A".repeat(256);
        BookRequestDTO dto = new BookRequestDTO(longTitle, "Author", "9780743273565", BigDecimal.TEN);

        Set<ConstraintViolation<BookRequestDTO>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Title is too long");
    }

    @Test
    void shouldFailValidation_WhenAuthorIsBlank() {
        BookRequestDTO dto = new BookRequestDTO("Title", "", "9780743273565", BigDecimal.TEN);

        Set<ConstraintViolation<BookRequestDTO>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Author is required");
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid-isbn", "123", "9781234567890123", "ABCDEFGHIJ"})
    void shouldFailValidation_WhenIsbnFormatIsInvalid(String invalidIsbn) {
        BookRequestDTO dto = new BookRequestDTO("Title", "Author", invalidIsbn, BigDecimal.TEN);

        Set<ConstraintViolation<BookRequestDTO>> violations = validator.validate(dto);

        assertThat(violations).anyMatch(v -> v.getMessage().contains("Invalid ISBN format"));
    }

    @Test
    void shouldFailValidation_WhenIsbnIsMissing() {
        BookRequestDTO dto = new BookRequestDTO("Title", "Author", null, BigDecimal.TEN);

        Set<ConstraintViolation<BookRequestDTO>> violations = validator.validate(dto);

        assertThat(violations).anyMatch(v -> v.getMessage().equals("ISBN is required"));
    }

    @Test
    void shouldFailValidation_WhenPriceIsNull() {
        BookRequestDTO dto = new BookRequestDTO("Title", "Author", "9780743273565", null);

        Set<ConstraintViolation<BookRequestDTO>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Price is required");
    }

    @Test
    void shouldFailValidation_WhenPriceIsNegative() {
        BookRequestDTO dto = new BookRequestDTO(
                "Title",
                "Author",
                "9780743273565",
                new BigDecimal("-1.00")
        );

        Set<ConstraintViolation<BookRequestDTO>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Price cannot be negative");
    }
}