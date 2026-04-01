package qm.service.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.util.UUID;

/**
 * CorrelationIdFilter -
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorrelationIdFilter implements GlobalFilter {
    private static final String CORRELATION_ID_HEADER = "X-Correlation-Id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String correlationId = exchange.getRequest().getHeaders().getFirst(CORRELATION_ID_HEADER);
        if (correlationId == null) correlationId = UUID.randomUUID().toString();

        String finalId = correlationId;
        log.debug("Correlation Id: {}", correlationId);
        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                .headers(headers -> headers.remove(CORRELATION_ID_HEADER))
                .header(CORRELATION_ID_HEADER, finalId)
                .build();

        exchange.getResponse().getHeaders()
                .add(CORRELATION_ID_HEADER, finalId);

        return chain.filter(exchange.mutate().request(mutatedRequest).build())
                .contextWrite(Context.of(CORRELATION_ID_HEADER, finalId));
    }
}
