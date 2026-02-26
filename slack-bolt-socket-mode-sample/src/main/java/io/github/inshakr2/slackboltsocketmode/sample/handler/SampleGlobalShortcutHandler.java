package io.github.inshakr2.slackboltsocketmode.sample.handler;

import com.slack.api.bolt.context.builtin.GlobalShortcutContext;
import com.slack.api.bolt.request.builtin.GlobalShortcutRequest;
import com.slack.api.bolt.response.Response;
import io.github.inshakr2.slackboltsocketmode.core.handler.shortcut.AbstractGlobalShortcutHandler;
import org.springframework.stereotype.Component;

@Component
public class SampleGlobalShortcutHandler extends AbstractGlobalShortcutHandler {

    @Override
    protected String getCallbackId() {
        return "sample-global-shortcut";
    }

    @Override
    protected Response handle(GlobalShortcutRequest req, GlobalShortcutContext ctx) {
        return ctx.ack();
    }
}
