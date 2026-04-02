package qm.service.gateway.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import qm.service.gateway.util.ResponseWriter;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * SecurityConfigTest -
 */
@SpringBootTest
@TestPropertySource(locations = "file:../.env.example")
class SecurityConfigTest {
    @Value("${REALM_CLIENT_ID}")
    private String realmClientId;

    @Value("${ISSUER_URI}")
    private String issuerUri;

    @Autowired
    private JwtProperties jwtProperties;
    @Autowired
    private ResponseWriter responseWriter;

    @Autowired
    private SecurityWebFilterChain securityWebFilterChain;

    @Autowired
    private CorsConfigurationSource corsConfigurationSource;

    @Autowired
    private ReactiveJwtDecoder jwtDecoder;

    @Autowired
    private ReactiveJwtAuthenticationConverter jwtAuthenticationConverter;

    // ============================================
    // SecurityWebFilterChain
    // ============================================

    @Test
    void securityWebFilterChain_notNull() {
        Assertions.assertNotNull(securityWebFilterChain);
    }

    // ============================================
    // CorsConfigurationSource
    // ============================================

    @Test
    void corsConfigurationSource_notNull() {
        Assertions.assertNotNull(corsConfigurationSource);
    }

    @Test
    void corsConfigurationSource_allowedMethods() {
        CorsConfiguration config = corsConfigurationSource
                .getCorsConfiguration(
                        MockServerWebExchange.from(
                                MockServerHttpRequest.get("/test").build()
                        )
                );

        Assertions.assertNotNull(config);
        Assertions.assertTrue(config.getAllowedMethods()
                .containsAll(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")));
    }

    @Test
    void corsConfigurationSource_allowedHeaders() {
        CorsConfiguration config = corsConfigurationSource
                .getCorsConfiguration(
                        MockServerWebExchange.from(
                                MockServerHttpRequest.get("/test").build()
                        )
                );

        Assertions.assertNotNull(config);
        Assertions.assertTrue(config.getAllowedHeaders()
                .containsAll(List.of("Authorization", "Content-Type", "X-Correlation-Id")));
    }

    @Test
    void corsConfigurationSource_allowCredentials() {
        CorsConfiguration config = corsConfigurationSource
                .getCorsConfiguration(
                        MockServerWebExchange.from(
                                MockServerHttpRequest.get("/test").build()
                        )
                );

        Assertions.assertNotNull(config);
        Assertions.assertTrue(config.getAllowCredentials());
    }

    @Test
    void corsConfigurationSource_maxAge() {
        CorsConfiguration config = corsConfigurationSource
                .getCorsConfiguration(
                        MockServerWebExchange.from(
                                MockServerHttpRequest.get("/test").build()
                        )
                );

        Assertions.assertNotNull(config);
        Assertions.assertEquals(3600L, config.getMaxAge());
    }

    @Test
    void corsConfigurationSource_exposedHeaders() {
        CorsConfiguration config = corsConfigurationSource
                .getCorsConfiguration(
                        MockServerWebExchange.from(
                                MockServerHttpRequest.get("/test").build()
                        )
                );

        Assertions.assertNotNull(config);
        Assertions.assertTrue(config.getExposedHeaders().contains("X-Correlation-Id"));
    }

    // ============================================
    // JwtDecoder
    // ============================================

    @Test
    void jwtDecoder_notNull() {
        Assertions.assertNotNull(jwtDecoder);
    }

    // ============================================
    // JwtAuthenticationConverter — extractRoles
    // ============================================

    @Test
    void extractRoles_whenNoResourceAccess_returnsEmpty() {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .subject("user-123")
                .issuer(issuerUri)
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(300))
                .build();

        StepVerifier.create(jwtAuthenticationConverter.convert(jwt))
                .assertNext(auth ->
                        Assertions.assertTrue(auth.getAuthorities().isEmpty())
                )
                .verifyComplete();
    }

    @Test
    void extractRoles_whenRolesExist_returnsWithPrefix() {
        Map<String, Object> clientAccess = Map.of("roles", List.of("USER", "ADMIN"));
        Map<String, Object> resourceAccess = Map.of(realmClientId, clientAccess);

        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .subject("user-123")
                .issuer(issuerUri)
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(300))
                .claim("resource_access", resourceAccess)
                .build();

        StepVerifier.create(jwtAuthenticationConverter.convert(jwt))
                .assertNext(auth -> {
                    List<String> authorities = auth.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .toList();
                    Assertions.assertTrue(authorities.contains("ROLE_USER"));
                    Assertions.assertTrue(authorities.contains("ROLE_ADMIN"));
                })
                .verifyComplete();
    }

    @Test
    void extractRoles_whenWrongClientId_returnsEmpty() {
        Map<String, Object> clientAccess = Map.of("roles", List.of("USER"));
        Map<String, Object> resourceAccess = Map.of("wrong-client-id", clientAccess);

        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .subject("user-123")
                .issuer(issuerUri)
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(300))
                .claim("resource_access", resourceAccess)
                .build();

        StepVerifier.create(jwtAuthenticationConverter.convert(jwt))
                .assertNext(auth ->
                        Assertions.assertTrue(auth.getAuthorities().isEmpty())
                )
                .verifyComplete();
    }

    @Test
    void extractRoles_whenEmptyRoles_returnsEmpty() {
        Map<String, Object> clientAccess = Map.of("roles", List.of());
        Map<String, Object> resourceAccess = Map.of(realmClientId, clientAccess);

        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .subject("user-123")
                .issuer(issuerUri)
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(300))
                .claim("resource_access", resourceAccess)
                .build();

        StepVerifier.create(jwtAuthenticationConverter.convert(jwt))
                .assertNext(auth ->
                        Assertions.assertTrue(auth.getAuthorities().isEmpty())
                )
                .verifyComplete();
    }

    // ============================================
    // responseMono — 401 / 403
    // ============================================

    @Test
    void responseMono_returns401_whenUnauthorized() {
        MockServerWebExchange exchange = MockServerWebExchange
                .from(MockServerHttpRequest.get("/test").build());

        SecurityConfig config = new SecurityConfig(jwtProperties, responseWriter);

        // тестуємо через endpoint
        StepVerifier.create(
                        responseWriter.writeJsonResponse(exchange, HttpStatus.UNAUTHORIZED,
                                """
                                        {"status":401,"error":"Unauthorized"}""")
                )
                .verifyComplete();

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED,
                exchange.getResponse().getStatusCode());
        Assertions.assertEquals(MediaType.APPLICATION_JSON,
                exchange.getResponse().getHeaders().getContentType());
    }
}
