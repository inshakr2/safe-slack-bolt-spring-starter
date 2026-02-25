package io.github.inshakr2.safeslackbolt.starter.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "safe.slack.bolt")
public class SafeSlackBoltProperties {

    private boolean enabled = true;
    private String botToken;
    private String appToken;
    private boolean socketModeEnabled = true;
    private boolean socketModeAutoStartup = true;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getBotToken() {
        return botToken;
    }

    public void setBotToken(String botToken) {
        this.botToken = botToken;
    }

    public String getAppToken() {
        return appToken;
    }

    public void setAppToken(String appToken) {
        this.appToken = appToken;
    }

    public boolean isSocketModeEnabled() {
        return socketModeEnabled;
    }

    public void setSocketModeEnabled(boolean socketModeEnabled) {
        this.socketModeEnabled = socketModeEnabled;
    }

    public boolean isSocketModeAutoStartup() {
        return socketModeAutoStartup;
    }

    public void setSocketModeAutoStartup(boolean socketModeAutoStartup) {
        this.socketModeAutoStartup = socketModeAutoStartup;
    }
}
