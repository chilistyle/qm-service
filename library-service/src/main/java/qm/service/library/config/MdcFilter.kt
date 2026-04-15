package qm.service.library.config

import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.container.ContainerRequestContext
import jakarta.ws.rs.container.ContainerRequestFilter
import jakarta.ws.rs.container.ContainerResponseContext
import jakarta.ws.rs.container.ContainerResponseFilter
import jakarta.ws.rs.ext.Provider
import org.jboss.logging.MDC
import java.util.*

/**
 * MdcFilter - 
 */
@Provider
@ApplicationScoped
class MdcFilter : ContainerRequestFilter, ContainerResponseFilter {

    companion object {
        private const val CORRELATION_ID_HEADER = "X-Correlation-Id"
    }

    override fun filter(requestContext: ContainerRequestContext) {
        val headerId = requestContext.getHeaderString(CORRELATION_ID_HEADER)

        // Використовуємо takeIf + isNotBlank, щоб відсіяти "" та "   "
        val correlationId = headerId?.takeIf { it.isNotBlank() }
            ?: UUID.randomUUID().toString()

        MDC.put(CORRELATION_ID_HEADER, correlationId)
        requestContext.setProperty(CORRELATION_ID_HEADER, correlationId)
    }

    override fun filter(
        requestContext: ContainerRequestContext,
        responseContext: ContainerResponseContext
    ) {
        try {
            val correlationId = requestContext.getProperty(CORRELATION_ID_HEADER) as? String
            correlationId?.let {
                responseContext.headers.add(CORRELATION_ID_HEADER, it)
            }
        } finally {
            // Видаляємо з MDC завжди, навіть якщо сталася помилка,
            // щоб не "отруїти" наступний запит у пулі потоків
            MDC.remove(CORRELATION_ID_HEADER)
        }
    }
}