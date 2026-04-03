package qm.service.book.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * BookRequestDTOTest -
 */
class BookRequestDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldBeValidWhenAllFieldsAreCorrect() {
        BookRequestDTO dto = new BookRequestDTO("The Witcher", "Sapkowski", "9781234567890", new BigDecimal("50.00"));

        Set<ConstraintViolation<BookRequestDTO>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    @Test
    void shouldHaveViolationsWhenFieldsAreEmpty() {
        BookRequestDTO dto = new BookRequestDTO("", "", "", null);

        Set<ConstraintViolation<BookRequestDTO>> violations = validator.validate(dto);

        assertThat(violations).hasSizeGreaterThanOrEqualTo(4);
    }

    @Test
    void shouldInvalidateIncorrectIsbn() {
        BookRequestDTO dto = new BookRequestDTO("Title", "Author", "invalid-isbn", new BigDecimal("10"));

        Set<ConstraintViolation<BookRequestDTO>> violations = validator.validate(dto);

        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .contains("Invalid ISBN format");
    }

    @Test
    void shouldInvalidateNegativePrice() {
        BookRequestDTO dto = new BookRequestDTO("Title", "Author", "9781234567890", new BigDecimal("-5"));

        Set<ConstraintViolation<BookRequestDTO>> violations = validator.validate(dto);

        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .contains("Price cannot be negative");
    }
}

