package io.github.inshakr2.safeslackbolt.core.handler.command;

import com.slack.api.bolt.App;
import com.slack.api.bolt.context.builtin.SlashCommandContext;
import com.slack.api.bolt.request.builtin.SlashCommandRequest;
import com.slack.api.bolt.response.Response;
import com.slack.api.methods.SlackApiException;
import io.github.inshakr2.safeslackbolt.core.handler.SafeBoltHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractSafeCommandHandler implements SafeBoltHandler {

    private static final Logger log = LoggerFactory.getLogger(AbstractSafeCommandHandler.class);

    protected abstract String getCommand();

    protected abstract Response handle(SlashCommandRequest req, SlashCommandContext ctx) throws Exception;

    public final Response handleSafely(SlashCommandRequest req, SlashCommandContext ctx) {
        try {
            return handle(req, ctx);
        } catch (Exception e) {
            log.error("Safe command handler failed. command={}, message={}", getCommand(), e.getMessage(), e);
            if (e instanceof SlackApiException) {
                SlackApiException slackApiException = (SlackApiException) e;
                if (slackApiException.getResponse() != null) {
                    log.error("Slack API error body={}", slackApiException.getResponse().body());
                }
            }
            return ctx.ack();
        }
    }

    @Override
    public final String getIdentifier() {
        return getCommand();
    }

    @Override
    public final void register(App app) {
        app.command(getCommand(), this::handleSafely);
    }
}
