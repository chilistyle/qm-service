package qm.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import reactor.core.publisher.Hooks;

/**
 * APIGatewayApplication -
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class APIGatewayApplication {
    public static void main(String[] args) {
        Hooks.enableAutomaticContextPropagation();
        SpringApplication.run(APIGatewayApplication.class, args);
    }
}
