package qm.service.book.exception;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import java.time.temporal.ChronoUnit;

/**
 * ErrorResponseTest -
 */
class ErrorResponseTest {

    @Test
    void shouldCreateErrorResponseWithAllFields() {
        // Given
        String code = "AUTH_001";
        String msg = "Access Denied";
        LocalDateTime now = LocalDateTime.now();

        // When
        ErrorResponse response = new ErrorResponse(code, msg, now);

        // Then
        assertThat(response.errorCode()).isEqualTo(code);
        assertThat(response.message()).isEqualTo(msg);
        assertThat(response.timestamp()).isEqualTo(now);
    }

    @Test
    void shouldCreateErrorResponseWithDefaultTimestamp() {
        // Given
        String code = "ERR_500";
        String msg = "Internal Server Error";

        // When
        ErrorResponse response = new ErrorResponse(code, msg);

        // Then
        assertThat(response.errorCode()).isEqualTo(code);
        assertThat(response.message()).isEqualTo(msg);
        assertThat(response.timestamp()).isNotNull();

        assertThat(response.timestamp()).isCloseTo(LocalDateTime.now(), within(1, ChronoUnit.SECONDS));
    }

    @Test
    void testEqualsAndHashCode() {
        LocalDateTime time = LocalDateTime.now();
        ErrorResponse res1 = new ErrorResponse("404", "Not Found", time);
        ErrorResponse res2 = new ErrorResponse("404", "Not Found", time);

        assertThat(res1).isEqualTo(res2);
        assertThat(res1.hashCode()).isEqualTo(res2.hashCode());
    }
}
