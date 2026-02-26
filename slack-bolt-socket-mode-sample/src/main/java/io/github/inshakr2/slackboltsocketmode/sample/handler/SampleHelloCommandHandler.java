package io.github.inshakr2.slackboltsocketmode.sample.handler;

import com.slack.api.bolt.context.builtin.SlashCommandContext;
import com.slack.api.bolt.request.builtin.SlashCommandRequest;
import com.slack.api.bolt.response.Response;
import io.github.inshakr2.slackboltsocketmode.core.handler.command.AbstractCommandHandler;
import org.springframework.stereotype.Component;

@Component
public class SampleHelloCommandHandler extends AbstractCommandHandler {

    @Override
    protected String getCommand() {
        return "/socket-mode-hello";
    }

    @Override
    protected Response handle(SlashCommandRequest req, SlashCommandContext ctx) {
        return ctx.ack("socket mode hello");
    }
}
