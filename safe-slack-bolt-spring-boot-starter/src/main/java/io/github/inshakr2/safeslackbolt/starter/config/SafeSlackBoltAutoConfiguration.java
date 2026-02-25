package io.github.inshakr2.safeslackbolt.starter.config;

import com.slack.api.bolt.App;
import com.slack.api.bolt.AppConfig;
import io.github.inshakr2.safeslackbolt.core.handler.SafeBoltHandler;
import io.github.inshakr2.safeslackbolt.starter.lifecycle.SocketModeLifecycle;
import io.github.inshakr2.safeslackbolt.starter.registry.HandlerRegistryValidator;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(App.class)
@EnableConfigurationProperties(SafeSlackBoltProperties.class)
public class SafeSlackBoltAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public HandlerRegistryValidator handlerRegistryValidator() {
        return new HandlerRegistryValidator();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "safe.slack.bolt", name = "enabled", havingValue = "true", matchIfMissing = true)
    public App safeSlackBoltApp(
            SafeSlackBoltProperties properties,
            ObjectProvider<SafeBoltHandler> handlerProvider,
            HandlerRegistryValidator validator
    ) {
        String botToken = properties.getBotToken();
        if (botToken == null || botToken.isBlank()) {
            throw new IllegalStateException("safe.slack.bolt.bot-token must not be blank when safe.slack.bolt.enabled=true");
        }

        App app = new App(AppConfig.builder()
                .singleTeamBotToken(botToken)
                .build());

        List<SafeBoltHandler> handlers = handlerProvider.stream()
                .sorted(Comparator.comparingInt(SafeBoltHandler::getOrder))
                .collect(Collectors.toList());

        validator.validate(handlers);
        handlers.forEach(handler -> handler.register(app));
        return app;
    }

    @Bean
    @ConditionalOnBean(App.class)
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "safe.slack.bolt", name = "socket-mode-enabled", havingValue = "true", matchIfMissing = true)
    public SocketModeLifecycle socketModeLifecycle(App app, SafeSlackBoltProperties properties) {
        String appToken = properties.getAppToken();
        if (appToken == null || appToken.isBlank()) {
            throw new IllegalStateException("safe.slack.bolt.app-token must not be blank when socket mode is enabled");
        }
        return new SocketModeLifecycle(app, properties);
    }

    @Bean
    @ConditionalOnBean(SocketModeLifecycle.class)
    @ConditionalOnProperty(prefix = "safe.slack.bolt", name = "socket-mode-auto-startup", havingValue = "true", matchIfMissing = true)
    public ApplicationRunner socketModeStartupRunner(SocketModeLifecycle lifecycle) {
        return args -> lifecycle.start();
    }
}
