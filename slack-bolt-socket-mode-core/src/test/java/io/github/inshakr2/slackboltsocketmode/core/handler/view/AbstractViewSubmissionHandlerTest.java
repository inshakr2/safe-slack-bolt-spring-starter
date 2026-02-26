package io.github.inshakr2.slackboltsocketmode.core.handler.view;

import com.slack.api.bolt.App;
import com.slack.api.bolt.context.builtin.ViewSubmissionContext;
import com.slack.api.bolt.handler.builtin.ViewSubmissionHandler;
import com.slack.api.bolt.request.builtin.ViewSubmissionRequest;
import com.slack.api.bolt.response.Response;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AbstractViewSubmissionHandlerTest {

    @Test
    void handleSafelyReturnsAckWhenHandleThrows() throws Exception {
        // Given
        Response ackResponse = Response.builder().statusCode(200).body("ack").build();
        ViewSubmissionContext context = mock(ViewSubmissionContext.class);
        when(context.ack()).thenReturn(ackResponse);

        AbstractViewSubmissionHandler handler = new AbstractViewSubmissionHandler() {
            @Override
            protected String getCallbackId() {
                return "test-callback";
            }

            @Override
            protected Response handle(ViewSubmissionRequest req, ViewSubmissionContext ctx) {
                throw new RuntimeException("boom");
            }
        };

        // When
        Response result = handler.handleSafely(mock(ViewSubmissionRequest.class), context);

        // Then
        assertThat(result).isSameAs(ackResponse);
        verify(context).ack();
    }

    @Test
    void registerBindsViewSubmissionHandlerToBoltApp() {
        // Given
        App app = mock(App.class);

        AbstractViewSubmissionHandler handler = new AbstractViewSubmissionHandler() {
            @Override
            protected String getCallbackId() {
                return "test-callback";
            }

            @Override
            protected Response handle(ViewSubmissionRequest req, ViewSubmissionContext ctx) {
                return Response.ok();
            }
        };

        // When
        handler.register(app);

        // Then
        verify(app).viewSubmission(ArgumentMatchers.eq("test-callback"), ArgumentMatchers.<ViewSubmissionHandler>any());
        assertThat(handler.getIdentifier()).isEqualTo("test-callback");
    }
}
