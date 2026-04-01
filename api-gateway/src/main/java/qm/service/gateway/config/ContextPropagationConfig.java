package qm.service.gateway.config;

import io.micrometer.context.ContextRegistry;
import jakarta.annotation.PostConstruct;
import org.slf4j.MDC;
import org.springframework.context.annotation.Configuration;

/**
 * ContextPropagationConfig -
 */
@Configuration
public class ContextPropagationConfig {
    private static final String CORRELATION_ID_HEADER = "X-Correlation-Id";

    @PostConstruct
    public void setup() {
        ContextRegistry.getInstance().registerThreadLocalAccessor(
                CORRELATION_ID_HEADER,
                () -> MDC.get(CORRELATION_ID_HEADER),
                value -> MDC.put(CORRELATION_ID_HEADER, value),
                () -> MDC.remove(CORRELATION_ID_HEADER)
        );
    }
}
