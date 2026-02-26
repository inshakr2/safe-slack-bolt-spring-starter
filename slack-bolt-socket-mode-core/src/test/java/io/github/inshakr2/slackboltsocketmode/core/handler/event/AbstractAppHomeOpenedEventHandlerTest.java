package io.github.inshakr2.slackboltsocketmode.core.handler.event;

import com.slack.api.app_backend.events.payload.EventsApiPayload;
import com.slack.api.bolt.App;
import com.slack.api.bolt.context.builtin.EventContext;
import com.slack.api.bolt.handler.BoltEventHandler;
import com.slack.api.bolt.response.Response;
import com.slack.api.model.event.AppHomeOpenedEvent;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AbstractAppHomeOpenedEventHandlerTest {

    @Test
    void handleSafelyReturnsAckWhenHandleThrows() throws Exception {
        // Given
        Response ackResponse = Response.builder().statusCode(200).body("ack").build();
        EventContext context = mock(EventContext.class);
        when(context.ack()).thenReturn(ackResponse);

        AbstractAppHomeOpenedEventHandler handler = new AbstractAppHomeOpenedEventHandler() {
            @Override
            protected String getEventIdentifier() {
                return "event:app_home_opened";
            }

            @Override
            protected Response handle(EventsApiPayload<AppHomeOpenedEvent> payload, EventContext ctx) {
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
    void registerBindsAppHomeOpenedHandlerToBoltApp() {
        // Given
        App app = mock(App.class);

        AbstractAppHomeOpenedEventHandler handler = new AbstractAppHomeOpenedEventHandler() {
            @Override
            protected String getEventIdentifier() {
                return "event:app_home_opened";
            }

            @Override
            protected Response handle(EventsApiPayload<AppHomeOpenedEvent> payload, EventContext ctx) {
                return Response.ok();
            }
        };

        // When
        handler.register(app);

        // Then
        verify(app).event(ArgumentMatchers.eq(AppHomeOpenedEvent.class), ArgumentMatchers.<BoltEventHandler<AppHomeOpenedEvent>>any());
        assertThat(handler.getIdentifier()).isEqualTo("event:app_home_opened");
    }
}
