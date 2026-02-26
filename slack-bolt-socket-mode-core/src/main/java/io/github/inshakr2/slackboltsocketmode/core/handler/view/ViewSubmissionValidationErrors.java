package io.github.inshakr2.slackboltsocketmode.core.handler.view;

import com.slack.api.bolt.context.builtin.ViewSubmissionContext;
import com.slack.api.bolt.response.Response;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class ViewSubmissionValidationErrors {

    private final Map<String, String> errors = new LinkedHashMap<>();

    private ViewSubmissionValidationErrors() {
    }

    public static ViewSubmissionValidationErrors of(String blockId, String message) {
        return new ViewSubmissionValidationErrors().add(blockId, message);
    }

    public ViewSubmissionValidationErrors add(String blockId, String message) {
        errors.put(Objects.requireNonNull(blockId, "blockId must not be null"),
                Objects.requireNonNull(message, "message must not be null"));
        return this;
    }

    public boolean isEmpty() {
        return errors.isEmpty();
    }

    public Map<String, String> asMap() {
        return Collections.unmodifiableMap(errors);
    }

    public Response ack(ViewSubmissionContext context) {
        return context.ackWithErrors(errors);
    }
}
