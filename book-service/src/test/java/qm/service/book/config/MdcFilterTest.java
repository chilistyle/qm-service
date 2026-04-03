package qm.service.book.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * MdcFilterTest -
 */
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

    @Test
    void shouldPutCorrelationIdInMdcAndContinueChain() throws IOException, ServletException {
        // Given
        String testId = "test-uuid-123";
        when(request.getHeader("X-Correlation-Id")).thenReturn(testId);

        doAnswer(invocation -> {
            assertThat(MDC.get("X-Correlation-Id")).isEqualTo(testId);
            return null;
        }).when(filterChain).doFilter(request, response);

        // When
        mdcFilter.doFilter(request, response, filterChain);

        // Then
        verify(filterChain, times(1)).doFilter(request, response);
        assertThat(MDC.get("X-Correlation-Id")).isNull();
    }

    @Test
    void shouldWorkWhenHeaderIsMissing() throws IOException, ServletException {
        // Given
        when(request.getHeader("X-Correlation-Id")).thenReturn(null);

        // When
        mdcFilter.doFilter(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        assertThat(MDC.get("X-Correlation-Id")).isNull();
    }
}

