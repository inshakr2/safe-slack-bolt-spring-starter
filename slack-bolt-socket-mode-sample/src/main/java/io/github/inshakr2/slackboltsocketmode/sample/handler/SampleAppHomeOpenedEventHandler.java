package io.github.inshakr2.slackboltsocketmode.sample.handler;

import com.slack.api.app_backend.events.payload.EventsApiPayload;
import com.slack.api.bolt.context.builtin.EventContext;
import com.slack.api.bolt.response.Response;
import com.slack.api.model.event.AppHomeOpenedEvent;
import io.github.inshakr2.slackboltsocketmode.core.handler.event.AbstractAppHomeOpenedEventHandler;
import org.springframework.stereotype.Component;

@Component
public class SampleAppHomeOpenedEventHandler extends AbstractAppHomeOpenedEventHandler {

    @Override
    protected String getEventIdentifier() {
        return "event:sample-app-home-opened";
    }

    @Override
    protected Response handle(EventsApiPayload<AppHomeOpenedEvent> payload, EventContext ctx) {
        return ctx.ack();
    }
}
