package qm.service.gateway.rest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

import java.util.Objects;
import java.util.concurrent.TimeoutException;

/**
 * FallbackControllerTest -
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "file:.env.example")
class FallbackControllerTest {

    @LocalServerPort
    private int port;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient
                .bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    @Test
    void fallback_whenNoException_returns503() {
        webTestClient.get()
                .uri("/fallback")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.SERVICE_UNAVAILABLE)
                .expectBody()
                .jsonPath("$.status").isEqualTo(503)
                .jsonPath("$.error").isEqualTo("Service Unavailable")
                .jsonPath("$.message").isEqualTo("Service temporarily unavailable")
                .jsonPath("$.timestamp").isNotEmpty()
                .jsonPath("$.path").isEqualTo("/fallback");
    }

    @Test
    void fallback_whenTimeoutException_returns504() {
        MockServerWebExchange exchange = MockServerWebExchange
                .from(MockServerHttpRequest.get("/fallback").build());

        exchange.getAttributes().put(
                ServerWebExchangeUtils.CIRCUITBREAKER_EXECUTION_EXCEPTION_ATTR,
                new TimeoutException("timeout")
        );

        FallbackController controller = new FallbackController();

        StepVerifier.create(controller.fallback(exchange))
                .assertNext(response -> {
                    Assertions.assertEquals(HttpStatus.GATEWAY_TIMEOUT,
                            response.getStatusCode());
                    Assertions.assertEquals("Service timeout",
                            Objects.requireNonNull(response.getBody()).get("message"));
                })
                .verifyComplete();
    }

    @Test
    void fallback_whenGenericException_returns503() {
        MockServerWebExchange exchange = MockServerWebExchange
                .from(MockServerHttpRequest.get("/fallback").build());

        exchange.getAttributes().put(
                ServerWebExchangeUtils.CIRCUITBREAKER_EXECUTION_EXCEPTION_ATTR,
                new RuntimeException("error")
        );

        FallbackController controller = new FallbackController();

        StepVerifier.create(controller.fallback(exchange))
                .assertNext(response ->
                        Assertions.assertEquals(HttpStatus.SERVICE_UNAVAILABLE,
                                response.getStatusCode())
                )
                .verifyComplete();
    }

    @Test
    void fallback_responseContainsAllRequiredFields() {
        webTestClient.get()
                .uri("/fallback")
                .exchange()
                .expectBody()
                .jsonPath("$.timestamp").isNotEmpty()
                .jsonPath("$.status").isNotEmpty()
                .jsonPath("$.error").isNotEmpty()
                .jsonPath("$.message").isNotEmpty()
                .jsonPath("$.path").isNotEmpty();
    }
}