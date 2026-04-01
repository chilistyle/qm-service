package qm.service.gateway.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RateLimiter.Response;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * GlobalRateLimitFilterTest -
 */
@ExtendWith(MockitoExtension.class)
class GlobalRateLimitFilterTest {

    @Mock
    private RedisRateLimiter rateLimiter;

    @Mock
    private KeyResolver ipKeyResolver;

    @Mock
    private WebFilterChain filterChain;

    private GlobalRateLimitFilter filter;

    @BeforeEach
    void setUp() {
        filter = new GlobalRateLimitFilter(rateLimiter, ipKeyResolver);
    }

    @Test
    void shouldAllowRequestWhenLimitNotExceeded() {
        String clientIp = "127.0.0.1";
        when(ipKeyResolver.resolve(any())).thenReturn(Mono.just(clientIp));
        when(rateLimiter.isAllowed("global", clientIp))
                .thenReturn(Mono.just(new Response(true, Collections.emptyMap())));
        when(filterChain.filter(any())).thenReturn(Mono.empty());

        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/public").build());

        StepVerifier.create(filter.filter(exchange, filterChain))
                .verifyComplete();

        verify(filterChain).filter(exchange);
    }

    @Test
    void shouldReturn429WhenLimitExceeded() {
        String clientIp = "127.0.0.1";
        when(ipKeyResolver.resolve(any())).thenReturn(Mono.just(clientIp));
        when(rateLimiter.isAllowed("global", clientIp))
                .thenReturn(Mono.just(new Response(false, Collections.emptyMap())));

        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/public").build());

        StepVerifier.create(filter.filter(exchange, filterChain))
                .verifyComplete();

        assert exchange.getResponse().getStatusCode() == HttpStatus.TOO_MANY_REQUESTS;
        verify(filterChain, never()).filter(any());
    }

    @Test
    void shouldReturn403WhenKeyIsEmpty() {
        when(ipKeyResolver.resolve(any())).thenReturn(Mono.empty());

        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/public").build());

        StepVerifier.create(filter.filter(exchange, filterChain))
                .verifyComplete();

        assert exchange.getResponse().getStatusCode() == HttpStatus.FORBIDDEN;
        verify(rateLimiter, never()).isAllowed(anyString(), anyString());
    }
}
