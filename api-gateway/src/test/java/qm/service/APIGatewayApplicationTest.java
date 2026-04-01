package qm.service;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;
import qm.service.gateway.APIGatewayApplication;
import reactor.core.publisher.Hooks;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
/**
 * APIGatewayApplicationTest -
 */
class APIGatewayApplicationTest {

    @Test
    void main_StartsApplicationAndEnablesHooks() {
        try (MockedStatic<Hooks> hooksMock = mockStatic(Hooks.class);
             MockedStatic<SpringApplication> springAppMock = mockStatic(SpringApplication.class)) {

            String[] args = new String[]{};
            APIGatewayApplication.main(args);

            hooksMock.verify(Hooks::enableAutomaticContextPropagation, times(1));

            springAppMock.verify(() ->
                            SpringApplication.run(eq(APIGatewayApplication.class), any(String[].class)),
                    times(1)
            );
        }
    }
}
