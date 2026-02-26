package io.github.inshakr2.slackboltsocketmode.core.handler.command;

import com.slack.api.bolt.App;
import com.slack.api.bolt.context.builtin.SlashCommandContext;
import com.slack.api.bolt.handler.builtin.SlashCommandHandler;
import com.slack.api.bolt.request.builtin.SlashCommandRequest;
import com.slack.api.bolt.response.Response;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AbstractCommandHandlerTest {

    @Test
    void handleSafelyReturnsAckWhenHandleThrows() throws Exception {
        // Given
        Response ackResponse = Response.builder().statusCode(200).body("ack").build();
        SlashCommandContext context = mock(SlashCommandContext.class);
        when(context.ack()).thenReturn(ackResponse);

        AbstractCommandHandler handler = new AbstractCommandHandler() {
            @Override
            protected String getCommand() {
                return "/test-command";
            }

            @Override
            protected Response handle(SlashCommandRequest req, SlashCommandContext ctx) {
                throw new RuntimeException("boom");
            }
        };

        // When
        Response result = handler.handleSafely(mock(SlashCommandRequest.class), context);

        // Then
        assertThat(result).isSameAs(ackResponse);
        verify(context).ack();
    }

    @Test
    void registerBindsCommandHandlerToBoltApp() {
        // Given
        App app = mock(App.class);

        AbstractCommandHandler handler = new AbstractCommandHandler() {
            @Override
            protected String getCommand() {
                return "/test-command";
            }

            @Override
            protected Response handle(SlashCommandRequest req, SlashCommandContext ctx) {
                return Response.ok();
            }
        };

        // When
        handler.register(app);

        // Then
        verify(app).command(ArgumentMatchers.eq("/test-command"), ArgumentMatchers.<SlashCommandHandler>any());
        assertThat(handler.getIdentifier()).isEqualTo("/test-command");
    }
}
