package io.github.inshakr2.slackboltsocketmode.sample.handler;

import com.slack.api.bolt.context.builtin.ViewSubmissionContext;
import com.slack.api.bolt.request.builtin.ViewSubmissionRequest;
import com.slack.api.bolt.response.Response;
import io.github.inshakr2.slackboltsocketmode.core.handler.view.AbstractViewSubmissionHandler;
import org.springframework.stereotype.Component;

@Component
public class SampleViewSubmissionHandler extends AbstractViewSubmissionHandler {

    @Override
    protected String getCallbackId() {
        return "socket-mode-view-submit";
    }

    @Override
    protected Response handle(ViewSubmissionRequest req, ViewSubmissionContext ctx) {
        return ctx.ack();
    }
}
