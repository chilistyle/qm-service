package qm.service.config;

import io.micrometer.context.ContextSnapshot;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import reactor.util.context.Context;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * ContextPropagationConfigTest -
 */
class ContextPropagationConfigTest {

    private static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
    private final ContextPropagationConfig config = new ContextPropagationConfig();

    @BeforeEach
    void setUp() {
        config.setup();
        MDC.clear();
    }

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void shouldPropagateCorrelationIdFromContextToMdc() {
        String expectedId = "test-correlation-123";
        Context reactorContext = Context.of(CORRELATION_ID_HEADER, expectedId);
        try (ContextSnapshot.Scope scope = ContextSnapshot.setAllThreadLocalsFrom(reactorContext)) {
            assertEquals(expectedId, MDC.get(CORRELATION_ID_HEADER),
                    "MDC should contain the correlation ID from Reactor Context");
        }
        assertNull(MDC.get(CORRELATION_ID_HEADER),
                "MDC should be cleared after scope is closed");
    }

    @Test
    void shouldHandleEmptyContextGracefully() {
        Context emptyContext = Context.empty();

        try (ContextSnapshot.Scope scope = ContextSnapshot.setAllThreadLocalsFrom(emptyContext)) {
            assertNull(MDC.get(CORRELATION_ID_HEADER));
        }
    }
}
