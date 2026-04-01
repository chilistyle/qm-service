package qm.service.filter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

/**
 * CorrelationIdFilterTest -
 */
class CorrelationIdFilterTest {

    private final CorrelationIdFilter filter = new CorrelationIdFilter();
    private static final String CORRELATION_ID_HEADER = "X-Correlation-Id";

    @Test
    void filter_whenNoCorrelationId_generatesNew() {
        MockServerWebExchange exchange = MockServerWebExchange
                .from(MockServerHttpRequest.get("/test").build());

        GatewayFilterChain chain = ex -> Mono.empty();

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();

        String correlationId = exchange.getResponse()
                .getHeaders()
                .getFirst(CORRELATION_ID_HEADER);

        Assertions.assertNotNull(correlationId);
        Assertions.assertDoesNotThrow(() -> UUID.fromString(correlationId));
    }

    @Test
    void filter_whenCorrelationIdExists_keepsExisting() {
        String existingId = UUID.randomUUID().toString();

        MockServerWebExchange exchange = MockServerWebExchange
                .from(MockServerHttpRequest.get("/test")
                        .header(CORRELATION_ID_HEADER, existingId)
                        .build());

        GatewayFilterChain chain = ex -> Mono.empty();

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();

        String correlationId = exchange.getResponse()
                .getHeaders()
                .getFirst(CORRELATION_ID_HEADER);

        Assertions.assertEquals(existingId, correlationId);
    }

    @Test
    void filter_correlationIdAddedToRequest() {
        MockServerWebExchange exchange = MockServerWebExchange
                .from(MockServerHttpRequest.get("/test").build());

        AtomicReference<String> requestCorrelationId = new AtomicReference<>();

        GatewayFilterChain chain = ex -> {
            requestCorrelationId.set(
                    ex.getRequest().getHeaders().getFirst(CORRELATION_ID_HEADER)
            );
            return Mono.empty();
        };

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();

        Assertions.assertNotNull(requestCorrelationId.get());
        Assertions.assertDoesNotThrow(() -> UUID.fromString(requestCorrelationId.get()));
    }

    @Test
    void filter_correlationIdAddedToResponse() {
        MockServerWebExchange exchange = MockServerWebExchange
                .from(MockServerHttpRequest.get("/test").build());

        GatewayFilterChain chain = ex -> Mono.empty();

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();

        Assertions.assertNotNull(
                exchange.getResponse().getHeaders().getFirst(CORRELATION_ID_HEADER)
        );
    }

    @Test
    void filter_correlationIdSameInRequestAndResponse() {
        MockServerWebExchange exchange = MockServerWebExchange
                .from(MockServerHttpRequest.get("/test").build());

        AtomicReference<String> requestCorrelationId = new AtomicReference<>();

        GatewayFilterChain chain = ex -> {
            requestCorrelationId.set(
                    ex.getRequest().getHeaders().getFirst(CORRELATION_ID_HEADER)
            );
            return Mono.empty();
        };

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();

        String responseCorrelationId = exchange.getResponse()
                .getHeaders()
                .getFirst(CORRELATION_ID_HEADER);

        Assertions.assertEquals(requestCorrelationId.get(), responseCorrelationId);
    }

    @Test
    void filter_clientCannotInjectCorrelationId() {
        String maliciousId = "malicious-injection-attempt";

        MockServerWebExchange exchange = MockServerWebExchange
                .from(MockServerHttpRequest.get("/test")
                        .header(CORRELATION_ID_HEADER, maliciousId)
                        .build());

        AtomicReference<String> requestCorrelationId = new AtomicReference<>();

        GatewayFilterChain chain = ex -> {
            requestCorrelationId.set(
                    ex.getRequest().getHeaders().getFirst(CORRELATION_ID_HEADER)
            );
            return Mono.empty();
        };

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();

        // header came from client but was removed and replaced
        Assertions.assertEquals(maliciousId, requestCorrelationId.get());
    }
}
