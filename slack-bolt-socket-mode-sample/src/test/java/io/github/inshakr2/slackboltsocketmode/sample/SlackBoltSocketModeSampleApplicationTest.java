package io.github.inshakr2.slackboltsocketmode.sample;

import io.github.inshakr2.slackboltsocketmode.core.handler.BoltHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "slack.bolt.socket-mode.enabled=false",
        "slack.bolt.socket-mode.socket-mode-enabled=false",
        "slack.bolt.socket-mode.socket-mode-auto-startup=false"
})
class SlackBoltSocketModeSampleApplicationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void contextLoads() {
        // Given
        // Spring Boot context

        // When
        Map<String, BoltHandler> handlers = applicationContext.getBeansOfType(BoltHandler.class);

        // Then
        assertThat(handlers).hasSize(6);
        assertThat(handlers.values().stream().map(BoltHandler::getIdentifier).collect(Collectors.toSet()))
                .hasSize(6);
    }
}
