package io.github.inshakr2.safeslackbolt.sample.handler;

import com.slack.api.bolt.context.builtin.GlobalShortcutContext;
import com.slack.api.bolt.request.builtin.GlobalShortcutRequest;
import com.slack.api.bolt.response.Response;
import io.github.inshakr2.safeslackbolt.core.handler.shortcut.AbstractSafeGlobalShortcutHandler;
import org.springframework.stereotype.Component;

@Component
public class SampleGlobalShortcutHandler extends AbstractSafeGlobalShortcutHandler {

    @Override
    protected String getCallbackId() {
        return "sample-global-shortcut";
    }

    @Override
    protected Response handle(GlobalShortcutRequest req, GlobalShortcutContext ctx) {
        return ctx.ack();
    }
}
