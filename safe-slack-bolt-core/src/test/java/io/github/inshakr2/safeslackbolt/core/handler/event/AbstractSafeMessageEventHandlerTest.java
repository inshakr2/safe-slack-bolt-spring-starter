package io.github.inshakr2.safeslackbolt.core.handler.event;

import com.slack.api.app_backend.events.payload.EventsApiPayload;
import com.slack.api.bolt.App;
import com.slack.api.bolt.context.builtin.EventContext;
import com.slack.api.bolt.handler.BoltEventHandler;
import com.slack.api.bolt.response.Response;
import com.slack.api.model.event.MessageEvent;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AbstractSafeMessageEventHandlerTest {

    @Test
    void handleSafelyReturnsAckWhenHandleThrows() throws Exception {
        // Given
        Response ackResponse = Response.builder().statusCode(200).body("ack").build();
        EventContext context = mock(EventContext.class);
        when(context.ack()).thenReturn(ackResponse);

        AbstractSafeMessageEventHandler handler = new AbstractSafeMessageEventHandler() {
            @Override
            protected Pattern getPattern() {
                return Pattern.compile("^hello$");
            }

            @Override
            protected Response handle(EventsApiPayload<MessageEvent> payload, EventContext ctx) {
                throw new RuntimeException("boom");
            }
        };

        // When
        Response result = handler.handleSafely(mock(EventsApiPayload.class), context);

        // Then
        assertThat(result).isSameAs(ackResponse);
        verify(context).ack();
    }

    @Test
    void registerBindsMessageEventHandlerToBoltApp() {
        // Given
        App app = mock(App.class);

        AbstractSafeMessageEventHandler handler = new AbstractSafeMessageEventHandler() {
            @Override
            protected Pattern getPattern() {
                return Pattern.compile("^hello$");
            }

            @Override
            protected Response handle(EventsApiPayload<MessageEvent> payload, EventContext ctx) {
                return Response.ok();
            }
        };

        // When
        handler.register(app);

        // Then
        verify(app).message(ArgumentMatchers.<Pattern>argThat(pattern -> pattern.pattern().equals("^hello$")),
                ArgumentMatchers.<BoltEventHandler<MessageEvent>>any());
        assertThat(handler.getIdentifier()).isEqualTo("message:^hello$");
    }
}
