package io.github.inshakr2.safeslackbolt.sample.handler;

import com.slack.api.bolt.context.builtin.SlashCommandContext;
import com.slack.api.bolt.request.builtin.SlashCommandRequest;
import com.slack.api.bolt.response.Response;
import io.github.inshakr2.safeslackbolt.core.handler.command.AbstractSafeCommandHandler;
import org.springframework.stereotype.Component;

@Component
public class SampleHelloCommandHandler extends AbstractSafeCommandHandler {

    @Override
    protected String getCommand() {
        return "/safe-hello";
    }

    @Override
    protected Response handle(SlashCommandRequest req, SlashCommandContext ctx) {
        return ctx.ack("safe hello");
    }
}
