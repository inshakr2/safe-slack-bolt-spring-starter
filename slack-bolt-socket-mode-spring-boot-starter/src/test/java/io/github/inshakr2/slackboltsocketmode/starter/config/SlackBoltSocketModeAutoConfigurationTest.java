package io.github.inshakr2.slackboltsocketmode.starter.config;

import com.slack.api.bolt.App;
import io.github.inshakr2.slackboltsocketmode.core.handler.BoltHandler;
import io.github.inshakr2.slackboltsocketmode.starter.lifecycle.SocketModeLifecycle;
import io.github.inshakr2.slackboltsocketmode.starter.registry.HandlerRegistryValidator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.util.ClassUtils;

import static org.assertj.core.api.Assertions.assertThat;

class SlackBoltSocketModeAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(SlackBoltSocketModeAutoConfiguration.class));

    @Test
    void createsAppAndRegistersHandlersWhenEnabled() {
        // Given
        RecordingHandler recordingHandler = new RecordingHandler("command:/socket-mode-test");

        // When
        contextRunner
                .withBean("recordingHandler", BoltHandler.class, () -> recordingHandler)
                .withPropertyValues(
                        "slack.bolt.socket-mode.enabled=true",
                        "slack.bolt.socket-mode.bot-token=xoxb-test-token",
                        "slack.bolt.socket-mode.socket-mode-enabled=false")
                .run(context -> {
                    // Then
                    assertThat(context).hasSingleBean(App.class);
                    assertThat(context).hasSingleBean(HandlerRegistryValidator.class);
                    assertThat(recordingHandler.isRegistered()).isTrue();
                });
    }

    @Test
    void failsFastWhenDuplicateIdentifiersExist() {
        // Given
        String duplicateIdentifier = "action:duplicate";

        // When
        contextRunner
                .withBean("handlerA", BoltHandler.class, () -> new RecordingHandler(duplicateIdentifier))
                .withBean("handlerB", BoltHandler.class, () -> new RecordingHandler(duplicateIdentifier))
                .withPropertyValues(
                        "slack.bolt.socket-mode.enabled=true",
                        "slack.bolt.socket-mode.bot-token=xoxb-test-token",
                        "slack.bolt.socket-mode.socket-mode-enabled=false")
                .run(context -> {
                    // Then
                    assertThat(context).hasFailed();
                    assertThat(context.getStartupFailure()).hasMessageContaining("Duplicate Slack handler identifier");
                });
    }

    @Test
    void createsSocketModeLifecycleWhenSocketModeEnabled() {
        // Given
        RecordingHandler recordingHandler = new RecordingHandler("view:callback");

        // When
        contextRunner
                .withBean("recordingHandler", BoltHandler.class, () -> recordingHandler)
                .withPropertyValues(
                        "slack.bolt.socket-mode.enabled=true",
                        "slack.bolt.socket-mode.bot-token=xoxb-test-token",
                        "slack.bolt.socket-mode.app-token=xapp-test-token",
                        "slack.bolt.socket-mode.socket-mode-enabled=true",
                        "slack.bolt.socket-mode.socket-mode-auto-startup=false")
                .run(context -> {
                    // Then
                    assertThat(context).hasSingleBean(SocketModeLifecycle.class);
                });
    }

    @Test
    void containsSocketModeRuntimeDependencies() {
        // Given
        ClassLoader classLoader = SlackBoltSocketModeAutoConfigurationTest.class.getClassLoader();

        // When
        boolean hasJavaxWebSocketApi = ClassUtils.isPresent("javax.websocket.DeploymentException", classLoader);
        boolean hasTyrusClient = ClassUtils.isPresent("org.glassfish.tyrus.client.ClientManager", classLoader);

        // Then
        assertThat(hasJavaxWebSocketApi).isTrue();
        assertThat(hasTyrusClient).isTrue();
    }

    private static final class RecordingHandler implements BoltHandler {
        private final String identifier;
        private boolean registered;

        private RecordingHandler(String identifier) {
            this.identifier = identifier;
        }

        @Override
        public String getIdentifier() {
            return identifier;
        }

        @Override
        public void register(App app) {
            this.registered = true;
        }

        private boolean isRegistered() {
            return registered;
        }
    }
}
