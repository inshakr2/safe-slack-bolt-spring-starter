package io.github.inshakr2.slackboltsocketmode.core.handler.shortcut;

import com.slack.api.bolt.App;
import com.slack.api.bolt.context.builtin.GlobalShortcutContext;
import com.slack.api.bolt.request.builtin.GlobalShortcutRequest;
import com.slack.api.bolt.response.Response;
import com.slack.api.methods.SlackApiException;
import io.github.inshakr2.slackboltsocketmode.core.handler.BoltHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractGlobalShortcutHandler implements BoltHandler {

    private static final Logger log = LoggerFactory.getLogger(AbstractGlobalShortcutHandler.class);

    protected abstract String getCallbackId();

    protected abstract Response handle(GlobalShortcutRequest req, GlobalShortcutContext ctx) throws Exception;

    public final Response handleSafely(GlobalShortcutRequest req, GlobalShortcutContext ctx) {
        try {
            return handle(req, ctx);
        } catch (Exception e) {
            log.error("Global shortcut handler failed. callbackId={}, message={}", getCallbackId(), e.getMessage(), e);
            if (e instanceof SlackApiException) {
                SlackApiException slackApiException = (SlackApiException) e;
                if (slackApiException.getResponse() != null) {
                    log.error("Slack API error body={}", slackApiException.getResponse().body());
                }
            }
            return ctx.ack();
        }
    }

    @Override
    public final String getIdentifier() {
        return getCallbackId();
    }

    @Override
    public final void register(App app) {
        app.globalShortcut(getCallbackId(), this::handleSafely);
    }
}
