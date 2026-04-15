package qm.service.book.config;

import jakarta.servlet.http.HttpServletRequest;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
        ServletRequestAttributes attributes = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(attributes);
    }

    @Test
    void checkRole_ShouldThrowForbidden_WhenHeaderIsMissing() {
        // Given
        when(request.getHeader("X-User-Roles")).thenReturn(null);

        // When & Then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, 
            () -> roleCheckAspect.checkRole(requiresRole));
        
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        assertEquals("Missing user roles", exception.getReason());
    }

    @Test
    void checkRole_ShouldThrowForbidden_WhenHeaderIsEmptyString() {
        // Given: Header is present but empty
        when(request.getHeader("X-User-Roles")).thenReturn("");

        // When & Then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, 
            () -> roleCheckAspect.checkRole(requiresRole));
        
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        assertEquals("Missing user roles", exception.getReason());
    }

    @Test
    void checkRole_ShouldThrowForbidden_WhenRolesDoNotMatch() {
        // Given
        when(request.getHeader("X-User-Roles")).thenReturn("ROLE_USER");
        when(requiresRole.value()).thenReturn(new String[]{"ADMIN"});

        // When & Then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, 
            () -> roleCheckAspect.checkRole(requiresRole));

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        assertEquals("Access denied", exception.getReason());
    }

    @Test
    void checkRole_ShouldAllowAccess_WhenRoleMatches() {
        // Given
        when(request.getHeader("X-User-Roles")).thenReturn("ROLE_ADMIN,ROLE_USER");
        when(requiresRole.value()).thenReturn(new String[]{"ADMIN"});

        // When & Then
        assertDoesNotThrow(() -> roleCheckAspect.checkRole(requiresRole));
    }

    @Test
    void checkRole_ShouldHandleWhitespaceInHeader() {
        // Given
        when(request.getHeader("X-User-Roles")).thenReturn("ROLE_USER, ROLE_ADMIN");
        when(requiresRole.value()).thenReturn(new String[]{"ADMIN"});

        // When & Then
        assertDoesNotThrow(() -> roleCheckAspect.checkRole(requiresRole));
    }

    @Test
    void checkRole_ShouldAllowAccess_WhenAnyOfMultipleRequiredRolesMatch() {
        // Given
        when(request.getHeader("X-User-Roles")).thenReturn("ROLE_MANAGER");
        when(requiresRole.value()).thenReturn(new String[]{"ADMIN", "MANAGER"});

        // When & Then
        assertDoesNotThrow(() -> roleCheckAspect.checkRole(requiresRole));
    }

    @Test
    void checkRole_ShouldDenyAccess_WhenAnnotationIncludesPrefixExplicitly() {
        /**
         * If the annotation includes "ROLE_", the aspect logic prepends another "ROLE_".
         * This test ensures the strict prefixing behavior is documented/tested.
         */
        // Given
        when(request.getHeader("X-User-Roles")).thenReturn("ROLE_ADMIN");
        when(requiresRole.value()).thenReturn(new String[]{"ROLE_ADMIN"});

        // When & Then: Resulting check is against "ROLE_ROLE_ADMIN"
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, 
            () -> roleCheckAspect.checkRole(requiresRole));
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        assertEquals("Access denied", exception.getReason());
    }

     @Test
    void checkRole_ShouldBeCaseSensitiveForRoles() {
        // Given: Header has lowercase, but Aspect prepends uppercase "ROLE_"
        // and matches against userRoles list
        when(request.getHeader("X-User-Roles")).thenReturn("role_admin");
        when(requiresRole.value()).thenReturn(new String[]{"ADMIN"});

        // When & Then: "ROLE_ADMIN" does not match "role_admin"
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, 
            () -> roleCheckAspect.checkRole(requiresRole));
        assertEquals("Access denied", exception.getReason());
    }

    @Test
    void checkRole_ShouldDenyAccess_WhenRoleIsOnlySubstring() {
        // Given: User has "ROLE_ADMINISTRATOR", but we strictly check for "ROLE_ADMIN"
        when(request.getHeader("X-User-Roles")).thenReturn("ROLE_ADMINISTRATOR");
        when(requiresRole.value()).thenReturn(new String[]{"ADMIN"});

        // When & Then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, 
            () -> roleCheckAspect.checkRole(requiresRole));
        assertEquals("Access denied", exception.getReason());
    }
}