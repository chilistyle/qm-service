package qm.service.library.config

import jakarta.ws.rs.container.ContainerRequestContext
import jakarta.ws.rs.container.ContainerResponseContext
import jakarta.ws.rs.core.MultivaluedHashMap
import jakarta.ws.rs.core.MultivaluedMap
import org.jboss.logging.MDC
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import java.util.*
import kotlin.test.*

@ExtendWith(MockitoExtension::class)
class MdcFilterTest {
    @InjectMocks
    private lateinit var mdcFilter: MdcFilter

    @Mock
    private lateinit var requestContext: ContainerRequestContext

    @Mock
    private lateinit var responseContext: ContainerResponseContext

    private val requestProperties = HashMap<String, Any>()
    private val responseHeaders: MultivaluedMap<String, Any> = MultivaluedHashMap()

    companion object {
        private const val CORRELATION_ID_HEADER = "X-Correlation-Id"
    }

    @BeforeEach
    fun setUp() {
        MDC.clear()
        requestProperties.clear()
        responseHeaders.clear()

        lenient().`when`(requestContext.getProperty(ArgumentMatchers.anyString())).thenAnswer { invocation ->
            requestProperties[invocation.getArgument<String>(0)]
        }

        lenient().doAnswer { invocation ->
            requestProperties[invocation.getArgument<String>(0)] = invocation.getArgument<Any>(1)
            null
        }.`when`(requestContext).setProperty(ArgumentMatchers.anyString(), ArgumentMatchers.any())

        lenient().`when`(responseContext.headers).thenReturn(responseHeaders)
    }

    @Test
    fun `request filter should generate new correlation ID when header is empty`() {
        // Given
        `when`(requestContext.getHeaderString(CORRELATION_ID_HEADER)).thenReturn("")

        // When
        mdcFilter.filter(requestContext)

        // Then
        val generatedId = MDC.get(CORRELATION_ID_HEADER) as? String
        assertNotNull(generatedId, "ID should not be null")
        assertTrue(generatedId.isNotBlank(), "Generated ID should not be blank, but was '$generatedId'")
    }

    @Test
    fun `request filter should generate new correlation ID when header is blank`() {
        // Given
        `when`(requestContext.getHeaderString(CORRELATION_ID_HEADER)).thenReturn("   ")

        // When
        mdcFilter.filter(requestContext)

        // Then
        val generatedCorrelationId = MDC.get(CORRELATION_ID_HEADER) as? String
        assertNotNull(generatedCorrelationId)
        assertTrue(generatedCorrelationId.isNotBlank())
    }

    @Test
    fun `request filter should put existing correlation ID into MDC and request properties`() {
        // Given
        val existingCorrelationId = UUID.randomUUID().toString()
        `when`(requestContext.getHeaderString(CORRELATION_ID_HEADER)).thenReturn(existingCorrelationId)

        // When
        mdcFilter.filter(requestContext)

        // Then
        assertEquals(existingCorrelationId, MDC.get(CORRELATION_ID_HEADER))
        assertEquals(existingCorrelationId, requestProperties[CORRELATION_ID_HEADER])
        verify(requestContext).setProperty(CORRELATION_ID_HEADER, existingCorrelationId)
    }

    @Test
    fun `request filter should generate new correlation ID when header is missing`() {
        // Given
        `when`(requestContext.getHeaderString(CORRELATION_ID_HEADER)).thenReturn(null)

        // When
        mdcFilter.filter(requestContext)

        // Then
        val generatedCorrelationId = MDC.get(CORRELATION_ID_HEADER) as? String
        assertNotNull(generatedCorrelationId)
        assertTrue(!generatedCorrelationId.isNullOrBlank())
        assertEquals(generatedCorrelationId, requestProperties[CORRELATION_ID_HEADER])
        verify(requestContext).setProperty(CORRELATION_ID_HEADER, generatedCorrelationId)
    }

    @Test
    fun `response filter should add correlation ID to response headers and clear MDC when present in request properties`() {
        // Given
        val correlationId = UUID.randomUUID().toString()
        requestProperties[CORRELATION_ID_HEADER] = correlationId // Simulate it being set by request filter

        // When
        mdcFilter.filter(requestContext, responseContext)

        // Then
        assertTrue(responseHeaders.containsKey(CORRELATION_ID_HEADER))
        assertEquals(correlationId, responseHeaders[CORRELATION_ID_HEADER]?.first())
        assertNull(MDC.get(CORRELATION_ID_HEADER))
    }

    @Test
    fun `response filter should not add correlation ID to response headers if not in request properties`() {
        // Given: correlation ID is NOT in requestProperties

        // When
        mdcFilter.filter(requestContext, responseContext)

        // Then
        assertFalse(responseHeaders.containsKey(CORRELATION_ID_HEADER))
        assertNull(MDC.get(CORRELATION_ID_HEADER))
    }

    @Test
    fun `response filter should clear MDC even if correlation ID is not in request properties`() {
        // Given
        MDC.put(CORRELATION_ID_HEADER, "some-id-in-mdc") // Simulate MDC having a value

        // When
        mdcFilter.filter(requestContext, responseContext)

        // Then
        assertNull(MDC.get(CORRELATION_ID_HEADER))
    }
}