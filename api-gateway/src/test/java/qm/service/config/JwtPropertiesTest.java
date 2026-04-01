package qm.service.config;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Set;

/**
 * JwtPropertiesTest -
 */
@SpringBootTest
@TestPropertySource(locations = "file:.env.example")
class JwtPropertiesTest {

    @Autowired
    private JwtProperties jwtProperties;

    @Test
    void jwtProperties_whenAllFieldsValid_createsSuccessfully() {
        Assertions.assertNotNull(jwtProperties);
        Assertions.assertNotNull(jwtProperties.jwkSetUri());
        Assertions.assertNotNull(jwtProperties.issuerUri());
        Assertions.assertNotNull(jwtProperties.realmClientId());
    }

    @Test
    void jwtProperties_jwkSetUri_notBlank() {
        Assertions.assertFalse(jwtProperties.jwkSetUri().isBlank());
    }

    @Test
    void jwtProperties_issuerUri_notBlank() {
        Assertions.assertFalse(jwtProperties.issuerUri().isBlank());
    }

    @Test
    void jwtProperties_realmClientId_notBlank() {
        Assertions.assertFalse(jwtProperties.realmClientId().isBlank());
    }

    @Test
    void validateJwksEndpoint_whenEndpointUnavailable_logsError() {
        Assertions.assertDoesNotThrow(jwtProperties::validateJwksEndpoint);
    }

    @Test
    void jwtProperties_validationConstraints() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        JwtProperties invalidProperties = new JwtProperties("", "", "");

        Set<ConstraintViolation<JwtProperties>> violations =
                validator.validate(invalidProperties);

        Assertions.assertEquals(3, violations.size());
    }
}
