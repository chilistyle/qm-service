package qm.service.library.config


import io.quarkus.runtime.StartupEvent
import io.quarkus.scheduler.Scheduled
import io.vertx.mutiny.core.Vertx
import io.vertx.mutiny.ext.web.client.WebClient
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.event.Observes
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.jboss.logging.Logger

/**
 * EurekaWatchdog - 
 */
@ApplicationScoped
class EurekaWatchdog(
    private val vertx: Vertx
) {
    private val log = Logger.getLogger(EurekaWatchdog::class.java)
    private lateinit var client: WebClient

    @ConfigProperty(name = "EUREKA_HOSTNAME", defaultValue = "localhost")
    lateinit var host: String

    @ConfigProperty(name = "EUREKA_PORT", defaultValue = "8761")
    lateinit var port: String

    @ConfigProperty(name = "quarkus.application.name", defaultValue = "library-service")
    lateinit var appName: String

    @ConfigProperty(name = "quarkus.http.port", defaultValue = "8100")
    lateinit var servicePort: String

    fun onStart(@Observes ev: StartupEvent) {
        client = WebClient.create(vertx)
    }

    @Scheduled(every = "30s", concurrentExecution = Scheduled.ConcurrentExecution.SKIP)
    fun forceHeartbeat() {
        val appId = appName.uppercase()
        val path = "/eureka/apps/$appId/$appName"

        client.put(port.toInt(), host, path)
            .send()
            .subscribe().with(
                { response ->
                    if (response.statusCode() == 404) {
                        log.warn("Instance not found in Eureka (after restart). Re-registering...")
                        registerAgain(appId)
                    } else {
                        log.info("Eureka heartbeat successful (Status: ${response.statusCode()})")
                    }
                },
                { err -> log.error("Failed to reach Eureka: ${err.message}") }
            )
    }

    private fun registerAgain(appId: String) {
        val currentIp = getLocalIp()

        val registrationBody = mapOf(
            "instance" to mapOf(
                "instanceId" to appName,
                "hostName" to host,
                "app" to appId,
                "ipAddr" to currentIp,
                "status" to "UP",
                "port" to mapOf("$" to servicePort.toInt(), "@enabled" to "true"),
                "dataCenterInfo" to mapOf(
                    "@class" to "com.netflix.appinfo.InstanceInfo\$DefaultDataCenterInfo",
                    "name" to "MyOwn"
                )
            )
        )

        client.post(port.toInt(), host, "/eureka/apps/$appId")
            .sendJson(registrationBody)
            .subscribe().with(
                { log.info("Re-registration successful with IP: $currentIp") },
                { e -> log.error("Re-registration failed: ${e.message}") }
            )
    }

    private fun getLocalIp(): String {
        return try {
            java.net.NetworkInterface.getNetworkInterfaces().asSequence()
                .filter { it.isUp && !it.isLoopback }
                .flatMap { it.inetAddresses.asSequence() }
                .filter { it is java.net.Inet4Address }
                .map { it.hostAddress }
                .firstOrNull() ?: "127.0.0.1"
        } catch (e: Exception) {
            "127.0.0.1"
        }
    }
}