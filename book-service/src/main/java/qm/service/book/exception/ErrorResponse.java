package qm.service.book.exception;

import java.time.LocalDateTime;

/**
 * ErrorResponse -
 */
public record ErrorResponse(
        String errorCode,
        String message,
        LocalDateTime timestamp
) {
    public ErrorResponse(String errorCode, String message) {
        this(errorCode, message, LocalDateTime.now());
    }
}
