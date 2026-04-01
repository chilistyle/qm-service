package qm.service.gateway.config;

import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

/**
 * JwtProperties -
 */
@Slf4j
@Validated
@ConfigurationProperties(prefix = "spring.security.oauth2.resourceserver.jwt")
public record JwtProperties(@NotBlank String jwkSetUri,
                            @NotBlank String issuerUri,
                            @NotBlank String realmClientId) {

    @PostConstruct
    public void validateJwksEndpoint() {
        WebClient.create()
                .get()
                .uri(jwkSetUri)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(5))
                .doOnError(e -> log.error("JWKS endpoint unavailable: {}", jwkSetUri))
                .subscribe();
    }
}
