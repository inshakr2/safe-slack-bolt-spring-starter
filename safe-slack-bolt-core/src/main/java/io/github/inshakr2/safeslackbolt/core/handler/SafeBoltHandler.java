package io.github.inshakr2.safeslackbolt.core.handler;

import com.slack.api.bolt.App;

public interface SafeBoltHandler {

    String getIdentifier();

    void register(App app);

    default int getOrder() {
        return 0;
    }
}
