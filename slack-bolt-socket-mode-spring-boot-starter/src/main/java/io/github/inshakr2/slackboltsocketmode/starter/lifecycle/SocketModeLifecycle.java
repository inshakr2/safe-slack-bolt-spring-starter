package io.github.inshakr2.slackboltsocketmode.starter.lifecycle;

import com.slack.api.bolt.App;
import com.slack.api.bolt.socket_mode.SocketModeApp;
import io.github.inshakr2.slackboltsocketmode.starter.config.SlackBoltSocketModeProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;

public class SocketModeLifecycle implements DisposableBean {

    private static final Logger log = LoggerFactory.getLogger(SocketModeLifecycle.class);

    private final App app;
    private final SlackBoltSocketModeProperties properties;
    private final Object monitor = new Object();
    private SocketModeApp socketModeApp;

    public SocketModeLifecycle(App app, SlackBoltSocketModeProperties properties) {
        this.app = app;
        this.properties = properties;
    }

    public void start() throws Exception {
        synchronized (monitor) {
            if (socketModeApp != null) {
                return;
            }
            socketModeApp = new SocketModeApp(properties.getAppToken(), app);
            socketModeApp.startAsync();
        }
    }

    public boolean isRunning() {
        synchronized (monitor) {
            return socketModeApp != null;
        }
    }

    @Override
    public void destroy() {
        synchronized (monitor) {
            if (socketModeApp == null) {
                return;
            }
            try {
                socketModeApp.close();
            } catch (Exception e) {
                log.warn("Failed to close SocketModeApp cleanly", e);
            } finally {
                socketModeApp = null;
            }
        }
    }
}
