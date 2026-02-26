package io.github.inshakr2.slackboltsocketmode.core.handler.event;

import com.slack.api.app_backend.events.payload.EventsApiPayload;
import com.slack.api.bolt.App;
import com.slack.api.bolt.context.builtin.EventContext;
import com.slack.api.bolt.response.Response;
import com.slack.api.methods.SlackApiException;
import com.slack.api.model.event.MessageEvent;
import io.github.inshakr2.slackboltsocketmode.core.handler.BoltHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

public abstract class AbstractMessageEventHandler implements BoltHandler {

    private static final Logger log = LoggerFactory.getLogger(AbstractMessageEventHandler.class);

    protected abstract Pattern getPattern();

    protected abstract Response handle(EventsApiPayload<MessageEvent> payload, EventContext ctx) throws Exception;

    public final Response handleSafely(EventsApiPayload<MessageEvent> payload, EventContext ctx) {
        try {
            return handle(payload, ctx);
        } catch (Exception e) {
            log.error("Message event handler failed. pattern={}, message={}", getPattern().pattern(), e.getMessage(), e);
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
        return "message:" + getPattern().pattern();
    }

    @Override
    public final void register(App app) {
        app.message(getPattern(), this::handleSafely);
    }
}
