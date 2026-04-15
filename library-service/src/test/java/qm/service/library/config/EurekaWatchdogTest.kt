package qm.service.library.config

import io.smallrye.mutiny.Uni
import io.vertx.mutiny.core.Vertx
import io.vertx.mutiny.core.buffer.Buffer
import io.vertx.mutiny.ext.web.client.HttpRequest
import io.vertx.mutiny.ext.web.client.HttpResponse
import io.vertx.mutiny.ext.web.client.WebClient
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.*
import org.mockito.Mockito.*
import java.lang.reflect.Field

class EurekaWatchdogTest {

    private lateinit var watchdog: EurekaWatchdog
    private val vertx = mock(Vertx::class.java)
    private val client = mock(WebClient::class.java)
    private val request = mock(HttpRequest::class.java) as HttpRequest<Buffer>
    private val response = mock(HttpResponse::class.java) as HttpResponse<Buffer>

    @BeforeEach
    fun setup() {
        watchdog = EurekaWatchdog(vertx)

        // Manually initialize lateinit properties for the unit test
        watchdog.host = "localhost"
        watchdog.port = "8761"
        watchdog.appName = "library-service"
        watchdog.servicePort = "8100"

        // Inject the mocked WebClient into the private lateinit property
        val clientField: Field = EurekaWatchdog::class.java.getDeclaredField("client")
        clientField.isAccessible = true
        clientField.set(watchdog, client)
    }

    @Test
    fun `forceHeartbeat should log success when Eureka returns 200`() {
        // Given
        whenever(client.put(anyInt(), anyString(), anyString())).thenReturn(request)
        whenever(request.send()).thenReturn(Uni.createFrom().item(response))
        whenever(response.statusCode()).thenReturn(200)

        // When
        watchdog.forceHeartbeat()

        // Then
        verify(client).put(8761, "localhost", "/eureka/apps/LIBRARY-SERVICE/library-service")
        verify(request).send()
    }

    @Test
    fun `forceHeartbeat should trigger re-registration when Eureka returns 404`() {
        // Given: Heartbeat fails with 404
        whenever(client.put(anyInt(), anyString(), anyString())).thenReturn(request)
        whenever(request.send()).thenReturn(Uni.createFrom().item(response))
        whenever(response.statusCode()).thenReturn(404)

        // And: Registration succeeds
        val postRequest = mock(HttpRequest::class.java) as HttpRequest<Buffer>
        val postResponse = mock(HttpResponse::class.java) as HttpResponse<Buffer>
        whenever(client.post(anyInt(), anyString(), anyString())).thenReturn(postRequest)
        whenever(postRequest.sendJson(any())).thenReturn(Uni.createFrom().item(postResponse))
        whenever(postResponse.statusCode()).thenReturn(204)

        // When
        watchdog.forceHeartbeat()

        // Then: It should have attempted to register again
        verify(client).post(eq(8761), eq("localhost"), eq("/eureka/apps/LIBRARY-SERVICE"))
        verify(postRequest).sendJson(any())
    }

    @Test
    fun `forceHeartbeat should log error when connection fails`() {
        // Given: Put fails with an exception
        whenever(client.put(anyInt(), anyString(), anyString())).thenReturn(request)
        whenever(request.send()).thenReturn(Uni.createFrom().failure(RuntimeException("Connection Refused")))

        // When
        watchdog.forceHeartbeat()

        // Then: Request was sent, error handled by the subscription subscriber
        verify(request).send()
    }

    @Test
    fun `registerAgain handles registration failure`() {
        // Given: Put returns 404
        whenever(client.put(anyInt(), anyString(), anyString())).thenReturn(request)
        whenever(request.send()).thenReturn(Uni.createFrom().item(response))
        whenever(response.statusCode()).thenReturn(404)

        // And: Registration POST returns 500
        val postRequest = mock(HttpRequest::class.java) as HttpRequest<Buffer>
        val postResponse = mock(HttpResponse::class.java) as HttpResponse<Buffer>
        whenever(client.post(anyInt(), anyString(), anyString())).thenReturn(postRequest)
        whenever(postRequest.sendJson(any())).thenReturn(Uni.createFrom().item(postResponse))
        whenever(postResponse.statusCode()).thenReturn(500)
        whenever(postResponse.bodyAsString()).thenReturn("Internal Server Error")

        // When
        watchdog.forceHeartbeat()

        // Then
        verify(postRequest).sendJson(any())
    }

    /**
     * Helper to bypass Kotlin's strict null checks with Mockito's whenever
     */
    private fun <T> whenever(methodCall: T): org.mockito.stubbing.OngoingStubbing<T> {
        return `when`(methodCall)
    }
}
