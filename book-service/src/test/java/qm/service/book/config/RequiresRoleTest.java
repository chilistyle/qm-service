package qm.service.book.config;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * RequiresRoleTest -
 */
class RequiresRoleTest {

    @RequiresRole({"ADMIN", "USER"})
    public void testMethod() {
    }

    @Test
    void annotationShouldStoreRoles() throws NoSuchMethodException {
        Method method = this.getClass().getMethod("testMethod");

        RequiresRole annotation = method.getAnnotation(RequiresRole.class);

        assertThat(annotation).isNotNull();
        assertThat(annotation.value()).containsExactlyInAnyOrder("ADMIN", "USER");
    }
}
