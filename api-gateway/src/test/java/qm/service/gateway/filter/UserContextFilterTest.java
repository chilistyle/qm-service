package qm.service.gateway.filter;

import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * UserContextFilterTest -
 */
class UserContextFilterTest {

    private final UserContextFilter filter = new UserContextFilter();

    @Test
    void shouldAddHeadersFromJwt() {
        // given
        Jwt jwt = Jwt.withTokenValue("token")
                .subject("user-123")
                .header("alg", "none")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .claim("scope", "ROLE_USER ROLE_ADMIN")
                .build();

        JwtAuthenticationToken auth = new JwtAuthenticationToken(
                jwt,
                List.of(
                        () -> "ROLE_USER",
                        () -> "ROLE_ADMIN"
                )
        );

        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/test").build()
        );

        GatewayFilterChain chain = ex -> {
            HttpHeaders headers = ex.getRequest().getHeaders();

            assertThat(headers.getFirst("X-User-Id")).isEqualTo("user-123");
            assertThat(headers.getFirst("X-User-Roles"))
                    .contains("ROLE_USER")
                    .contains("ROLE_ADMIN");

            return Mono.empty();
        };

        // when
        filter.filter(exchange, chain)
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth))
                .block();
    }

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
