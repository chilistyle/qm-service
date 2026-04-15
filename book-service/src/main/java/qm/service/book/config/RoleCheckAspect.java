package qm.service.book.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;

@Aspect
@Component
@Slf4j
public class RoleCheckAspect {

    @Before("@annotation(requiresRole)")
    public void checkRole(RequiresRole requiresRole) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                .currentRequestAttributes()).getRequest();

        String rolesHeader = request.getHeader("X-User-Roles");
        log.debug("Roles header: {}", rolesHeader);

        if (rolesHeader == null || rolesHeader.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Missing user roles");
        }

        List<String> userRoles = Arrays.stream(rolesHeader.split(","))
                .map(String::trim)
                .toList();

        boolean hasAccess = Arrays.stream(requiresRole.value())
                .map(role -> "ROLE_" + role)
                .anyMatch(userRoles::contains);

        if (!hasAccess) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
    }
}