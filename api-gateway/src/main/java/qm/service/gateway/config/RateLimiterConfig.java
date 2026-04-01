package qm.service.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

/**
 * RateLimiterConfig -
 */
@Configuration
public class RateLimiterConfig {
    @Bean
    public RedisRateLimiter redisRateLimiter() {
        //for testing config required
        RedisRateLimiter.Config config = new RedisRateLimiter.Config()
                .setReplenishRate(10)
                .setBurstCapacity(20);
        RedisRateLimiter limiter = new RedisRateLimiter(10, 20);
        limiter.getConfig().put("default", config);
        return limiter;
    }

    @Bean
    public KeyResolver ipKeyResolver() {
        return exchange ->
                Mono.just(exchange.getRequest().getRemoteAddress().getAddress().getHostAddress());

    }
}
