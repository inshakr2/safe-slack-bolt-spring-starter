package io.github.inshakr2.slackboltsocketmode.core.handler.action;

import com.slack.api.bolt.App;
import com.slack.api.bolt.context.builtin.ActionContext;
import com.slack.api.bolt.handler.builtin.BlockActionHandler;
import com.slack.api.bolt.request.builtin.BlockActionRequest;
import com.slack.api.bolt.response.Response;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AbstractBlockActionHandlerTest {

    @Test
    void handleSafelyReturnsAckWhenHandleThrows() throws Exception {
        // Given
        Response ackResponse = Response.builder().statusCode(200).body("ack").build();
        ActionContext context = mock(ActionContext.class);
        when(context.ack()).thenReturn(ackResponse);

        AbstractBlockActionHandler handler = new AbstractBlockActionHandler() {
            @Override
            protected String getActionId() {
                return "test-action";
            }

            @Override
            protected Response handle(BlockActionRequest req, ActionContext ctx) {
                throw new RuntimeException("boom");
            }
        };

        // When
        Response result = handler.handleSafely(mock(BlockActionRequest.class), context);

        // Then
        assertThat(result).isSameAs(ackResponse);
        verify(context).ack();
    }

    @Test
    void registerBindsBlockActionHandlerToBoltApp() {
        // Given
        App app = mock(App.class);

        AbstractBlockActionHandler handler = new AbstractBlockActionHandler() {
            @Override
            protected String getActionId() {
                return "test-action";
            }

            @Override
            protected Response handle(BlockActionRequest req, ActionContext ctx) {
                return Response.ok();
            }
        };

        // When
        handler.register(app);

        // Then
        verify(app).blockAction(ArgumentMatchers.eq("test-action"), ArgumentMatchers.<BlockActionHandler>any());
        assertThat(handler.getIdentifier()).isEqualTo("test-action");
    }
}
