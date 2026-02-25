package io.github.inshakr2.safeslackbolt.starter.registry;

import io.github.inshakr2.safeslackbolt.core.handler.SafeBoltHandler;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HandlerRegistryValidator {

    public void validate(List<SafeBoltHandler> handlers) {
        Set<String> identifiers = new HashSet<>();
        for (SafeBoltHandler handler : handlers) {
            String identifier = handler.getIdentifier();
            if (!identifiers.add(identifier)) {
                throw new IllegalStateException("Duplicate Slack handler identifier: " + identifier);
            }
        }
    }
}
