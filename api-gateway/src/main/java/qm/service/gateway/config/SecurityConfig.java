package qm.service.gateway.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import qm.service.gateway.util.ResponseWriter;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

/**
 * SecurityConfig -
 */
@Slf4j
@Configuration
@EnableWebFluxSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private static final String UNAUTHORIZED_BODY = """
            {"status":401,"error":"Unauthorized"}""";

    private static final String FORBIDDEN_BODY = """
            {"status":403,"error":"Forbidden"}""";


    @Value("${app.cors.allowed-origins}")
    private List<String> allowedOrigins;
    private final JwtProperties jwtProperties;
    private final ResponseWriter responseWriter;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/fallback").permitAll()
                        .pathMatchers("/actuator/**").permitAll()
                        .pathMatchers("/public/**").permitAll()
                        .anyExchange().authenticated()
                )
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtDecoder(jwtDecoder())
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())
                        )
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint((exchange, ex) ->
                                responseWriter.writeJsonResponse(exchange, HttpStatus.UNAUTHORIZED, UNAUTHORIZED_BODY)
                        )
                        .accessDeniedHandler((exchange, denied) ->
                                responseWriter.writeJsonResponse(exchange, HttpStatus.FORBIDDEN, FORBIDDEN_BODY)
                        )
                )
                .build();
    }

    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        NimbusReactiveJwtDecoder jwtDecoder = NimbusReactiveJwtDecoder
                .withJwkSetUri(jwtProperties.jwkSetUri())
                .build();
        jwtDecoder.setJwtValidator(
                JwtValidators.createDefaultWithIssuer(jwtProperties.issuerUri())
        );
        return jwtDecoder;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(allowedOrigins);
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Correlation-Id", "X-Requested-With", "Accept"));
        configuration.setExposedHeaders(List.of("X-Correlation-Id", "X-User-Id", "X-User-Roles"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public ReactiveJwtAuthenticationConverter jwtAuthenticationConverter() {
        ReactiveJwtAuthenticationConverter converter = new ReactiveJwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt ->
                Flux.fromIterable(extractRoles(jwt))
        );
        return converter;
    }

    private List<SimpleGrantedAuthority> extractRoles(Jwt jwt) {
        // Keycloak
        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");

        if (resourceAccess == null || !resourceAccess.containsKey(jwtProperties.realmClientId())) {
            log.debug("No roles found for clientId: {}", jwtProperties.realmClientId());
            return List.of();
        }

        Object clientAccessObj = resourceAccess.get(jwtProperties.realmClientId());
        if (!(clientAccessObj instanceof Map<?, ?> clientAccess)) {
            return List.of();
        }

        Object rolesObj = clientAccess.get("roles");
        if (!(rolesObj instanceof List<?> rawRoles)) {
            return List.of();
        }

        List<SimpleGrantedAuthority> authorities = rawRoles.stream()
                .filter(r -> r instanceof String)
                .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                .toList();

        log.debug("Extracted roles: {}", authorities);
        return authorities;
    }
}
