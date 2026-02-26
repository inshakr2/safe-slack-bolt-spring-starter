package io.github.inshakr2.slackboltsocketmode.core.handler;

import com.slack.api.bolt.App;

public interface BoltHandler {

    String getIdentifier();

    void register(App app);

    default int getOrder() {
        return 0;
    }
}
