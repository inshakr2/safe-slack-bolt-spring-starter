package io.github.inshakr2.safeslackbolt.sample.handler;

import com.slack.api.bolt.context.builtin.ActionContext;
import com.slack.api.bolt.request.builtin.BlockActionRequest;
import com.slack.api.bolt.response.Response;
import io.github.inshakr2.safeslackbolt.core.handler.action.AbstractSafeBlockActionHandler;
import org.springframework.stereotype.Component;

@Component
public class SamplePingActionHandler extends AbstractSafeBlockActionHandler {

    @Override
    protected String getActionId() {
        return "safe-ping-action";
    }

    @Override
    protected Response handle(BlockActionRequest req, ActionContext ctx) {
        return ctx.ack();
    }
}
