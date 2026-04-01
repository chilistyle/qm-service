package qm.service.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
/**
 * ResponseWriterTest -
 */
class ResponseWriterTest {

    private ResponseWriter responseWriter;
    private MockServerWebExchange exchange;

    @BeforeEach
    void setUp() {
        responseWriter = new ResponseWriter();
        // Creating mock request for exchange
        exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/").build());
    }

    @Test
    void writeJsonResponse_SetsCorrectStatusAndHeaders() {
        String body = "{\"error\":\"unauthorized\"}";
        HttpStatus status = HttpStatus.UNAUTHORIZED;

        Mono<Void> result = responseWriter.writeJsonResponse(exchange, status, body);

        StepVerifier.create(result)
                .verifyComplete();

        // Status check
        assertEquals(status, exchange.getResponse().getStatusCode());

        // Header check Content-Type
        assertEquals(MediaType.APPLICATION_JSON, exchange.getResponse().getHeaders().getContentType());
    }

    @Test
    void writeJsonResponse_WritesCorrectBody() {
        String expectedBody = "{\"status\":\"ok\"}";

        Mono<Void> result = responseWriter.writeJsonResponse(exchange, HttpStatus.OK, expectedBody);

        StepVerifier.create(result)
                .verifyComplete();

        // Body check
        StepVerifier.create(exchange.getResponse().getBody())
                .assertNext(buffer -> {
                    byte[] bytes = new byte[buffer.readableByteCount()];
                    buffer.read(bytes);
                    DataBufferUtils.release(buffer);
                    String actualBody = new String(bytes, StandardCharsets.UTF_8);
                    assertEquals(expectedBody, actualBody);
                })
                .verifyComplete();
    }
}