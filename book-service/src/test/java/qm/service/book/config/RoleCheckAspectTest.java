package qm.service.book.config;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

/**
 * RoleCheckAspectTest -
 */
@ExtendWith(MockitoExtension.class)
class RoleCheckAspectTest {

    @InjectMocks
    private RoleCheckAspect roleCheckAspect;

    @Mock
    private HttpServletRequest request;

    @Mock
    private RequiresRole requiresRole;

    @BeforeEach
    void setUp() {
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void shouldAllowAccess_WhenUserHasRequiredRole() {
        // Given
        when(requiresRole.value()).thenReturn(new String[]{"ADMIN"});
        when(request.getHeader("X-User-Roles")).thenReturn("ROLE_USER,ROLE_ADMIN");

        // When & Then
        assertDoesNotThrow(() -> roleCheckAspect.checkRole(requiresRole));
    }

    @Test
    void shouldThrowForbidden_WhenUserLacksRole() {
        // Given
        when(requiresRole.value()).thenReturn(new String[]{"ADMIN"});
        when(request.getHeader("X-User-Roles")).thenReturn("ROLE_USER");

        // When & Then
        assertThatThrownBy(() -> roleCheckAspect.checkRole(requiresRole))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status", HttpStatus.FORBIDDEN)
                .hasMessageContaining("Access denied");
    }

    @Test
    void shouldThrowForbidden_WhenHeaderIsMissing() {
        // Given
        when(request.getHeader("X-User-Roles")).thenReturn(null);

        // When & Then
        assertThatThrownBy(() -> roleCheckAspect.checkRole(requiresRole))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status", HttpStatus.FORBIDDEN)
                .hasMessageContaining("Missing user roles");
    }
}

