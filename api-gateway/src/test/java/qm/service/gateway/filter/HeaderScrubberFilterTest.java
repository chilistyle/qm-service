package qm.service.gateway.filter;

import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.*;

/**
 * HeaderScrubberFilterTest -
 */
class HeaderScrubberFilterTest {

    private final HeaderScrubberFilter filter = new HeaderScrubberFilter();

    @Test
    void shouldRemoveUserHeaders() {
        // given
        MockServerHttpRequest request = MockServerHttpRequest.get("/test")
                .header("X-User-Id", "123")
                .header("X-User-Roles", "ADMIN")
                .header("Other", "value")
                .build();

        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        GatewayFilterChain chain = mock(GatewayFilterChain.class);
        when(chain.filter(any())).thenReturn(Mono.empty());

        // when
        filter.filter(exchange, chain).block();

        // then
        verify(chain).filter(argThat(ex -> {
            HttpHeaders headers = ex.getRequest().getHeaders();
            return !headers.containsHeader("X-User-Id")
                    && !headers.containsHeader("X-User-Roles")
                    && headers.containsHeader("Other");
        }));
    }
}
