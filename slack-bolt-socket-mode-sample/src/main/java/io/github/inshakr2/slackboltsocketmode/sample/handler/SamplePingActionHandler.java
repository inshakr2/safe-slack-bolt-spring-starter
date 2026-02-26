package io.github.inshakr2.slackboltsocketmode.sample.handler;

import com.slack.api.bolt.context.builtin.ActionContext;
import com.slack.api.bolt.request.builtin.BlockActionRequest;
import com.slack.api.bolt.response.Response;
import io.github.inshakr2.slackboltsocketmode.core.handler.action.AbstractBlockActionHandler;
import org.springframework.stereotype.Component;

@Component
public class SamplePingActionHandler extends AbstractBlockActionHandler {

    @Override
    protected String getActionId() {
        return "socket-mode-ping-action";
    }

    @Override
    protected Response handle(BlockActionRequest req, ActionContext ctx) {
        return ctx.ack();
    }
}
