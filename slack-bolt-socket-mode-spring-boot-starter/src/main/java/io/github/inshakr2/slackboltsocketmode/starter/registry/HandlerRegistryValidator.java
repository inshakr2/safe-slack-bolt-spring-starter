package io.github.inshakr2.slackboltsocketmode.starter.registry;

import io.github.inshakr2.slackboltsocketmode.core.handler.BoltHandler;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HandlerRegistryValidator {

    public void validate(List<BoltHandler> handlers) {
        Set<String> identifiers = new HashSet<>();
        for (BoltHandler handler : handlers) {
            String identifier = handler.getIdentifier();
            if (!identifiers.add(identifier)) {
                throw new IllegalStateException("Duplicate Slack handler identifier: " + identifier);
            }
        }
    }
}
