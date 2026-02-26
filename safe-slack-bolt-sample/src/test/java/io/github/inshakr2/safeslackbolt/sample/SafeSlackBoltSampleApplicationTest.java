package io.github.inshakr2.safeslackbolt.sample;

import io.github.inshakr2.safeslackbolt.core.handler.SafeBoltHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "safe.slack.bolt.enabled=false",
        "safe.slack.bolt.socket-mode-enabled=false",
        "safe.slack.bolt.socket-mode-auto-startup=false"
})
class SafeSlackBoltSampleApplicationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void contextLoads() {
        // Given
        // Spring Boot context

        // When
        Map<String, SafeBoltHandler> handlers = applicationContext.getBeansOfType(SafeBoltHandler.class);

        // Then
        assertThat(handlers).hasSize(6);
        assertThat(handlers.values().stream().map(SafeBoltHandler::getIdentifier).collect(Collectors.toSet()))
                .hasSize(6);
    }
}
