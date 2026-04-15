package qm.service.book.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MdcFilterTest {

    @InjectMocks
    private MdcFilter mdcFilter;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private static final String CORRELATION_ID_HEADER = "X-Correlation-Id";

    @BeforeEach
    void setUp() {
        MDC.clear();
    }

    @Test
    void shouldAddCorrelationIdToMdc_WhenHeaderIsPresent() throws ServletException, IOException {
        // Given
        String correlationId = UUID.randomUUID().toString();
        when(request.getHeader(CORRELATION_ID_HEADER)).thenReturn(correlationId);

        // Verify that the MDC contains the value during chain.doFilter execution
        doAnswer(invocation -> {
            assertEquals(correlationId, MDC.get(CORRELATION_ID_HEADER));
            return null;
        }).when(filterChain).doFilter(request, response);

        // When
        mdcFilter.doFilter(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        // After filter completion, MDC should be empty due to try-with-resources
        assertNull(MDC.get(CORRELATION_ID_HEADER));
    }

    @Test
    void shouldNotAddCorrelationIdToMdc_WhenHeaderIsMissing() throws ServletException, IOException {
        // Given
        when(request.getHeader(CORRELATION_ID_HEADER)).thenReturn(null);

        // Verify that MDC contains a generated UUID during execution
        doAnswer(invocation -> {
            assertNotNull(MDC.get(CORRELATION_ID_HEADER));
            assertFalse(MDC.get(CORRELATION_ID_HEADER).isEmpty());
            return null;
        }).when(filterChain).doFilter(request, response);

        // When
        mdcFilter.doFilter(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        assertNull(MDC.get(CORRELATION_ID_HEADER));
    }

    @Test
    void shouldNotAddCorrelationIdToMdc_WhenHeaderIsEmpty() throws ServletException, IOException {
        // Given
        when(request.getHeader(CORRELATION_ID_HEADER)).thenReturn("   ");

        // Verify that MDC contains a generated UUID during execution
        doAnswer(invocation -> {
            assertNotNull(MDC.get(CORRELATION_ID_HEADER));
            return null;
        }).when(filterChain).doFilter(request, response);

        // When
        mdcFilter.doFilter(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        assertNull(MDC.get(CORRELATION_ID_HEADER));
    }

    @Test
    void shouldCleanupMdc_EvenWhenChainThrowsException() throws ServletException, IOException {
        // Given
        String correlationId = "error-id";
        when(request.getHeader(CORRELATION_ID_HEADER)).thenReturn(correlationId);
        doThrow(new RuntimeException("Filter failure")).when(filterChain).doFilter(request, response);

        // When & Then
        assertThrows(RuntimeException.class, () -> mdcFilter.doFilter(request, response, filterChain));
        
        // Ensure cleanup happened even after exception
        assertNull(MDC.get(CORRELATION_ID_HEADER));
    }
}