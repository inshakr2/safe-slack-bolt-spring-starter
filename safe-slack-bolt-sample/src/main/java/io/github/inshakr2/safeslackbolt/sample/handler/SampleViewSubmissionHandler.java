package io.github.inshakr2.safeslackbolt.sample.handler;

import com.slack.api.bolt.context.builtin.ViewSubmissionContext;
import com.slack.api.bolt.request.builtin.ViewSubmissionRequest;
import com.slack.api.bolt.response.Response;
import io.github.inshakr2.safeslackbolt.core.handler.view.AbstractSafeViewSubmissionHandler;
import org.springframework.stereotype.Component;

@Component
public class SampleViewSubmissionHandler extends AbstractSafeViewSubmissionHandler {

    @Override
    protected String getCallbackId() {
        return "safe-view-submit";
    }

    @Override
    protected Response handle(ViewSubmissionRequest req, ViewSubmissionContext ctx) {
        return ctx.ack();
    }
}
