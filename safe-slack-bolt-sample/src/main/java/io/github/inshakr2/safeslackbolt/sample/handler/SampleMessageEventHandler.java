package io.github.inshakr2.safeslackbolt.sample.handler;

import com.slack.api.app_backend.events.payload.EventsApiPayload;
import com.slack.api.bolt.context.builtin.EventContext;
import com.slack.api.bolt.response.Response;
import com.slack.api.model.event.MessageEvent;
import io.github.inshakr2.safeslackbolt.core.handler.event.AbstractSafeMessageEventHandler;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class SampleMessageEventHandler extends AbstractSafeMessageEventHandler {

    @Override
    protected Pattern getPattern() {
        return Pattern.compile("^safe-sample-message$");
    }

    @Override
    protected Response handle(EventsApiPayload<MessageEvent> payload, EventContext ctx) {
        return ctx.ack();
    }
}
