package qm.service.book.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class MdcFilter implements Filter {
    private static final String CORRELATION_ID_HEADER = "X-Correlation-Id";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        String correlationId = req.getHeader(CORRELATION_ID_HEADER);
        log.debug("correlationId={}", correlationId);
        try (MDC.MDCCloseable ignored = MDC.putCloseable(CORRELATION_ID_HEADER, correlationId)) {
            chain.doFilter(request, response);
        }
    }
}