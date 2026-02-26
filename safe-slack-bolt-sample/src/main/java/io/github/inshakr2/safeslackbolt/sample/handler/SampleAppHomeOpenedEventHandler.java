package io.github.inshakr2.safeslackbolt.sample.handler;

import com.slack.api.app_backend.events.payload.EventsApiPayload;
import com.slack.api.bolt.context.builtin.EventContext;
import com.slack.api.bolt.response.Response;
import com.slack.api.model.event.AppHomeOpenedEvent;
import io.github.inshakr2.safeslackbolt.core.handler.event.AbstractSafeAppHomeOpenedEventHandler;
import org.springframework.stereotype.Component;

@Component
public class SampleAppHomeOpenedEventHandler extends AbstractSafeAppHomeOpenedEventHandler {

    @Override
    protected String getEventIdentifier() {
        return "event:sample-app-home-opened";
    }

    @Override
    protected Response handle(EventsApiPayload<AppHomeOpenedEvent> payload, EventContext ctx) {
        return ctx.ack();
    }
}
