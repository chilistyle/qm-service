package qm.service.book;

import qm.service.book.config.MdcFilter;
import org.junit.jupiter.api.Test;
import qm.service.book.config.RoleCheckAspect;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;

@SpringBootTest(
    properties = {
        "spring.cloud.config.enabled=false",
        "spring.flyway.enabled=false",
        "spring.main.allow-bean-definition-overriding=true",
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration,org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration"
    }
)
class BookServiceApplicationTests {

    @Autowired
    private ApplicationContext context;

    /**
     * Test to ensure that the Spring application context loads successfully.
     * If the context fails to load, this test will fail, indicating a configuration issue.
     */
    @Test
    void contextLoads() {
        assertThat(context).isNotNull();
    }

    /**
     * This test ensures that the main method can be invoked without throwing exceptions.
     * It's a basic sanity check for the application's entry point.
     */
    @Test
    void refreshAutoConfigurationIsExcluded() {
        // The RefreshAutoConfiguration typically defines a 'refreshScope' (RefreshScope) bean.
        // We verify that it is not present in the context.
        org.junit.jupiter.api.Assertions.assertThrows(NoSuchBeanDefinitionException.class, 
            () -> context.getBean("refreshScope"));
    }

    @Test
    void mdcFilterBeanExists() {
        assertThat(context.getBean(MdcFilter.class)).isNotNull();
    }

    @Test
    void roleCheckAspectBeanExists() {
        assertThat(context.getBean(RoleCheckAspect.class)).isNotNull();
    }
}