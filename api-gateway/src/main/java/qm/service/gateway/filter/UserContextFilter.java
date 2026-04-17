package qm.service.gateway.filter;

import jakarta.ws.rs.core.HttpHeaders;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

/**
 * UserContextFilter -
 */
@Slf4j
@Component
@Order(10) //must be after security filters, even Ordered.HIGHEST_PRECEDENCE after security config
public class UserContextFilter implements GlobalFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.debug("UserContextFilter filter");
        if (exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION) == null) {
            return chain.filter(exchange);
        }
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .filter(auth -> auth instanceof JwtAuthenticationToken)
                .cast(JwtAuthenticationToken.class)
                .map(jwtAuth -> {
                    String userId = jwtAuth.getToken().getSubject();
                    String roles = jwtAuth.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(Collectors.joining(","));
                    log.debug("userId=" + userId);
                    log.debug("roles=" + roles);
                    return exchange.getRequest().mutate()
                            .header("X-User-Id", userId)
                            .header("X-User-Roles", roles)
                            .build();
                })
                .defaultIfEmpty(exchange.getRequest())
                .flatMap(mutatedRequest -> chain.filter(exchange.mutate().request(mutatedRequest).build()));
    }
}
