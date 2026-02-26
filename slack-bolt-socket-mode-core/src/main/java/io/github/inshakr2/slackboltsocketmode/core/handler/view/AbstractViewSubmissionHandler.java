package io.github.inshakr2.slackboltsocketmode.core.handler.view;

import com.slack.api.bolt.App;
import com.slack.api.bolt.context.builtin.ViewSubmissionContext;
import com.slack.api.bolt.request.builtin.ViewSubmissionRequest;
import com.slack.api.bolt.response.Response;
import com.slack.api.methods.SlackApiException;
import io.github.inshakr2.slackboltsocketmode.core.handler.BoltHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractViewSubmissionHandler implements BoltHandler {

    private static final Logger log = LoggerFactory.getLogger(AbstractViewSubmissionHandler.class);

    protected abstract String getCallbackId();

    protected abstract Response handle(ViewSubmissionRequest req, ViewSubmissionContext ctx) throws Exception;

    public final Response handleSafely(ViewSubmissionRequest req, ViewSubmissionContext ctx) {
        try {
            return handle(req, ctx);
        } catch (Exception e) {
            log.error("View submission handler failed. callbackId={}, message={}", getCallbackId(), e.getMessage(), e);
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
        return getCallbackId();
    }

    @Override
    public final void register(App app) {
        app.viewSubmission(getCallbackId(), this::handleSafely);
    }
}
