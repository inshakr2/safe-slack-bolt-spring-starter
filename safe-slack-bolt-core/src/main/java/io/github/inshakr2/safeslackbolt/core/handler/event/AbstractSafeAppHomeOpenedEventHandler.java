package io.github.inshakr2.safeslackbolt.core.handler.event;

import com.slack.api.app_backend.events.payload.EventsApiPayload;
import com.slack.api.bolt.App;
import com.slack.api.bolt.context.builtin.EventContext;
import com.slack.api.bolt.response.Response;
import com.slack.api.methods.SlackApiException;
import com.slack.api.model.event.AppHomeOpenedEvent;
import io.github.inshakr2.safeslackbolt.core.handler.SafeBoltHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractSafeAppHomeOpenedEventHandler implements SafeBoltHandler {

    private static final Logger log = LoggerFactory.getLogger(AbstractSafeAppHomeOpenedEventHandler.class);

    protected abstract String getEventIdentifier();

    protected abstract Response handle(EventsApiPayload<AppHomeOpenedEvent> payload, EventContext ctx) throws Exception;

    public final Response handleSafely(EventsApiPayload<AppHomeOpenedEvent> payload, EventContext ctx) {
        try {
            return handle(payload, ctx);
        } catch (Exception e) {
            log.error("Safe app_home_opened event handler failed. identifier={}, message={}", getEventIdentifier(), e.getMessage(), e);
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
        return getEventIdentifier();
    }

    @Override
    public final void register(App app) {
        app.event(AppHomeOpenedEvent.class, this::handleSafely);
    }
}
