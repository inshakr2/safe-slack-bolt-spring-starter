package io.github.inshakr2.safeslackbolt.starter.config;

import com.slack.api.bolt.App;
import io.github.inshakr2.safeslackbolt.core.handler.SafeBoltHandler;
import io.github.inshakr2.safeslackbolt.starter.lifecycle.SocketModeLifecycle;
import io.github.inshakr2.safeslackbolt.starter.registry.HandlerRegistryValidator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class SafeSlackBoltAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(SafeSlackBoltAutoConfiguration.class));

    @Test
    void createsAppAndRegistersHandlersWhenEnabled() {
        // Given
        RecordingHandler recordingHandler = new RecordingHandler("command:/safe-test");

        // When
        contextRunner
                .withBean("recordingHandler", SafeBoltHandler.class, () -> recordingHandler)
                .withPropertyValues(
                        "safe.slack.bolt.enabled=true",
                        "safe.slack.bolt.bot-token=xoxb-test-token",
                        "safe.slack.bolt.socket-mode-enabled=false")
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
                .withBean("handlerA", SafeBoltHandler.class, () -> new RecordingHandler(duplicateIdentifier))
                .withBean("handlerB", SafeBoltHandler.class, () -> new RecordingHandler(duplicateIdentifier))
                .withPropertyValues(
                        "safe.slack.bolt.enabled=true",
                        "safe.slack.bolt.bot-token=xoxb-test-token",
                        "safe.slack.bolt.socket-mode-enabled=false")
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
                .withBean("recordingHandler", SafeBoltHandler.class, () -> recordingHandler)
                .withPropertyValues(
                        "safe.slack.bolt.enabled=true",
                        "safe.slack.bolt.bot-token=xoxb-test-token",
                        "safe.slack.bolt.app-token=xapp-test-token",
                        "safe.slack.bolt.socket-mode-enabled=true",
                        "safe.slack.bolt.socket-mode-auto-startup=false")
                .run(context -> {
                    // Then
                    assertThat(context).hasSingleBean(SocketModeLifecycle.class);
                });
    }

    private static final class RecordingHandler implements SafeBoltHandler {
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
