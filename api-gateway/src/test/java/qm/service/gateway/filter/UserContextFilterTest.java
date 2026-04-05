package qm.service.gateway.filter;

import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * UserContextFilterTest -
 */
class UserContextFilterTest {

    private final UserContextFilter filter = new UserContextFilter();

    @Test
    void shouldNotAddHeadersWhenNoAuth() {
        UserContextFilter filter = new UserContextFilter();

        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/test").build()
        );

        GatewayFilterChain chain = ex -> {
            HttpHeaders headers = ex.getRequest().getHeaders();

            assertThat(headers.containsHeader("X-User-Id")).isFalse();
            assertThat(headers.containsHeader("X-User-Roles")).isFalse();

            return Mono.empty();
        };

        filter.filter(exchange, chain).block();
    }

    @Test
    void shouldIgnoreNonJwtAuthentication() {
        UserContextFilter filter = new UserContextFilter();

        TestingAuthenticationToken auth =
                new TestingAuthenticationToken("user", "pass", "ROLE_USER");

        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/test").build()
        );

        GatewayFilterChain chain = ex -> {
            HttpHeaders headers = ex.getRequest().getHeaders();

            assertThat(headers.containsHeader("X-User-Id")).isFalse();
            assertThat(headers.containsHeader("X-User-Roles")).isFalse();

            return Mono.empty();
        };

        filter.filter(exchange, chain)
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth))
                .block();
    }
}
