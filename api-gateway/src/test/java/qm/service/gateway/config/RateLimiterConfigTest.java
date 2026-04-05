package qm.service.gateway.config;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.test.StepVerifier;

import java.net.InetSocketAddress;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * RateLimiterConfigTest -
 */
class RateLimiterConfigTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(RateLimiterConfig.class))
            .withBean(ReactiveStringRedisTemplate.class, () -> Mockito.mock(ReactiveStringRedisTemplate.class))
            .withBean("redisRequestRateLimiterScript", RedisScript.class, () -> Mockito.mock(RedisScript.class));

    @Test
    void ipKeyResolverShouldResolveCorrectIp() {
        contextRunner.run(context -> {
            assertThat(context).hasBean("ipKeyResolver");
            KeyResolver resolver = context.getBean("ipKeyResolver", KeyResolver.class);

            String testIp = "123.123.123.123";
            MockServerHttpRequest request = MockServerHttpRequest.get("/")
                    .remoteAddress(new InetSocketAddress(testIp, 80))
                    .build();
            MockServerWebExchange exchange = MockServerWebExchange.from(request);

            StepVerifier.create(resolver.resolve(exchange))
                    .expectNext(testIp)
                    .verifyComplete();
        });
    }
}