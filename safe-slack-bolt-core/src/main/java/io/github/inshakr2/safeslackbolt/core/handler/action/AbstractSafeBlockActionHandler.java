package io.github.inshakr2.safeslackbolt.core.handler.action;

import com.slack.api.bolt.App;
import com.slack.api.bolt.context.builtin.ActionContext;
import com.slack.api.bolt.request.builtin.BlockActionRequest;
import com.slack.api.bolt.response.Response;
import com.slack.api.methods.SlackApiException;
import io.github.inshakr2.safeslackbolt.core.handler.SafeBoltHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractSafeBlockActionHandler implements SafeBoltHandler {

    private static final Logger log = LoggerFactory.getLogger(AbstractSafeBlockActionHandler.class);

    protected abstract String getActionId();

    protected abstract Response handle(BlockActionRequest req, ActionContext ctx) throws Exception;

    public final Response handleSafely(BlockActionRequest req, ActionContext ctx) {
        try {
            return handle(req, ctx);
        } catch (Exception e) {
            log.error("Safe block action handler failed. actionId={}, message={}", getActionId(), e.getMessage(), e);
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
        return getActionId();
    }

    @Override
    public final void register(App app) {
        app.blockAction(getActionId(), this::handleSafely);
    }
}
