package qm.service.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import reactor.core.publisher.Hooks;

/**
 * APIGatewayApplication -
 */
@SpringBootApplication
@ConfigurationPropertiesScan
@EnableDiscoveryClient
public class APIGatewayApplication {
    public static void main(String[] args) {
        Hooks.enableAutomaticContextPropagation();
        SpringApplication.run(APIGatewayApplication.class, args);
    }
}
