package io.github.inshakr2.slackboltsocketmode.core.experimental.modal;

import com.slack.api.bolt.context.Context;
import com.slack.api.bolt.response.Response;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.response.views.ViewsOpenResponse;
import com.slack.api.model.view.View;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class SlackModalOpener {

    private static final String DEFAULT_FAILURE_MESSAGE = "Failed to open modal";
    private static final String DEFAULT_FAILURE_CODE = "SLACK_MODAL_OPEN_FAILED";
    private static final String UNKNOWN_OPEN_ERROR = "unknown_error";

    private SlackModalOpener() {
    }

    public static boolean open(Context ctx, String triggerId, View modal) throws IOException, SlackApiException {
        return openResult(ctx, triggerId, modal).isOpened();
    }

    public static SlackModalOpenResult openResult(Context ctx, String triggerId, View modal)
            throws IOException, SlackApiException {
        Objects.requireNonNull(ctx, "ctx must not be null");
        Objects.requireNonNull(modal, "modal must not be null");
        String normalizedTriggerId = requireText(triggerId, "triggerId");

        ViewsOpenResponse response = ctx.client().viewsOpen(r -> r
                .triggerId(normalizedTriggerId)
                .view(modal));

        if (response.isOk()) {
            return SlackModalOpenResult.success(response.getWarning());
        }
        return SlackModalOpenResult.failure(
                normalizeError(response.getError()),
                response.getWarning()
        );
    }

    public static Response openOrAck(Context ctx, String triggerId, View modal) throws IOException, SlackApiException {
        Objects.requireNonNull(ctx, "ctx must not be null");
        SlackModalOpenResult result = openResult(ctx, triggerId, modal);
        if (result.isOpened()) {
            return ctx.ack();
        }
        return ctx.ackWithJson(failurePayload(result));
    }

    private static Map<String, String> failurePayload(SlackModalOpenResult result) {
        Map<String, String> payload = new LinkedHashMap<>();
        payload.put("code", DEFAULT_FAILURE_CODE);
        payload.put("message", DEFAULT_FAILURE_MESSAGE);
        payload.put("error", result.getError());
        if (result.getWarning() != null) {
            payload.put("warning", result.getWarning());
        }
        return payload;
    }

    private static String normalizeError(String error) {
        if (error == null) {
            return UNKNOWN_OPEN_ERROR;
        }
        String trimmed = error.trim();
        if (trimmed.isEmpty()) {
            return UNKNOWN_OPEN_ERROR;
        }
        return trimmed;
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
