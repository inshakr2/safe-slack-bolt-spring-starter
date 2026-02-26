package io.github.inshakr2.slackboltsocketmode.sample.handler;

import com.slack.api.app_backend.events.payload.EventsApiPayload;
import com.slack.api.bolt.context.builtin.EventContext;
import com.slack.api.bolt.response.Response;
import com.slack.api.model.event.MessageEvent;
import io.github.inshakr2.slackboltsocketmode.core.handler.event.AbstractMessageEventHandler;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class SampleMessageEventHandler extends AbstractMessageEventHandler {

    @Override
    protected Pattern getPattern() {
        return Pattern.compile("^socket-mode-sample-message$");
    }

    @Override
    protected Response handle(EventsApiPayload<MessageEvent> payload, EventContext ctx) {
        return ctx.ack();
    }
}
