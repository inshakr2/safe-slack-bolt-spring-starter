package io.github.inshakr2.slackboltsocketmode.core.handler.shortcut;

import com.slack.api.bolt.App;
import com.slack.api.bolt.context.builtin.GlobalShortcutContext;
import com.slack.api.bolt.handler.builtin.GlobalShortcutHandler;
import com.slack.api.bolt.request.builtin.GlobalShortcutRequest;
import com.slack.api.bolt.response.Response;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AbstractGlobalShortcutHandlerTest {

    @Test
    void handleSafelyReturnsAckWhenHandleThrows() throws Exception {
        // Given
        Response ackResponse = Response.builder().statusCode(200).body("ack").build();
        GlobalShortcutContext context = mock(GlobalShortcutContext.class);
        when(context.ack()).thenReturn(ackResponse);

        AbstractGlobalShortcutHandler handler = new AbstractGlobalShortcutHandler() {
            @Override
            protected String getCallbackId() {
                return "shortcut:sample";
            }

            @Override
            protected Response handle(GlobalShortcutRequest req, GlobalShortcutContext ctx) {
                throw new RuntimeException("boom");
            }
        };

        // When
        Response result = handler.handleSafely(mock(GlobalShortcutRequest.class), context);

        // Then
        assertThat(result).isSameAs(ackResponse);
        verify(context).ack();
    }

    @Test
    void registerBindsGlobalShortcutHandlerToBoltApp() {
        // Given
        App app = mock(App.class);

        AbstractGlobalShortcutHandler handler = new AbstractGlobalShortcutHandler() {
            @Override
            protected String getCallbackId() {
                return "shortcut:sample";
            }

            @Override
            protected Response handle(GlobalShortcutRequest req, GlobalShortcutContext ctx) {
                return Response.ok();
            }
        };

        // When
        handler.register(app);

        // Then
        verify(app).globalShortcut(ArgumentMatchers.eq("shortcut:sample"), ArgumentMatchers.<GlobalShortcutHandler>any());
        assertThat(handler.getIdentifier()).isEqualTo("shortcut:sample");
    }
}
