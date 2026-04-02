package qm.service.book.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * BookRequestDTO -
 */
public record BookRequestDTO(
        @NotBlank(message = "Title is required")
        @Size(max = 255, message = "Title is too long")
        String title,

        @NotBlank(message = "Author is required")
        String author,

        @NotBlank(message = "ISBN is required")
        @Pattern(regexp = "^(97(8|9))?\\d{9}(\\d|X)$", message = "Invalid ISBN format")
        String isbn,

        @NotNull(message = "Price is required")
        @PositiveOrZero(message = "Price cannot be negative")
        BigDecimal price
) {}
