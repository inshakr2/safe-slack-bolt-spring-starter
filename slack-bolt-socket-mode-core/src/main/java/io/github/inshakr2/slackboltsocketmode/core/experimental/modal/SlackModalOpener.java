package io.github.inshakr2.slackboltsocketmode.core.experimental.modal;

import com.slack.api.bolt.context.Context;
import com.slack.api.bolt.response.Response;
import com.slack.api.methods.SlackApiException;
import com.slack.api.model.view.View;

import java.io.IOException;
import java.util.Objects;

public final class SlackModalOpener {

    private static final int DEFAULT_FAILURE_STATUS_CODE = 500;
    private static final String DEFAULT_FAILURE_MESSAGE = "Failed to open modal";

    private SlackModalOpener() {
    }

    public static boolean open(Context ctx, String triggerId, View modal) throws IOException, SlackApiException {
        Objects.requireNonNull(ctx, "ctx must not be null");
        Objects.requireNonNull(modal, "modal must not be null");
        String normalizedTriggerId = requireText(triggerId, "triggerId");

        return ctx.client().viewsOpen(r -> r
                .triggerId(normalizedTriggerId)
                .view(modal))
                .isOk();
    }

    public static Response openOrAck(Context ctx, String triggerId, View modal) throws IOException, SlackApiException {
        return openOrAck(ctx, triggerId, modal, DEFAULT_FAILURE_STATUS_CODE, DEFAULT_FAILURE_MESSAGE);
    }

    public static Response openOrAck(Context ctx,
                                     String triggerId,
                                     View modal,
                                     int failureStatusCode,
                                     String failureMessage) throws IOException, SlackApiException {
        Objects.requireNonNull(ctx, "ctx must not be null");
        if (open(ctx, triggerId, modal)) {
            return ctx.ack();
        }
        return ctx.ackWithJson(failureResponse(failureStatusCode, failureMessage));
    }

    public static Response failureResponse(int statusCode, String message) {
        if (statusCode < 100 || statusCode > 599) {
            throw new SlackModalValidationException("statusCode must be between 100 and 599");
        }
        return Response.builder()
                .statusCode(statusCode)
                .body(requireText(message, "message"))
                .build();
    }

    private static String requireText(String value, String fieldName) {
        Objects.requireNonNull(value, fieldName + " must not be null");
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw new SlackModalValidationException(fieldName + " must not be blank");
        }
        return trimmed;
    }
}
