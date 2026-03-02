package io.github.inshakr2.slackboltsocketmode.core.experimental.modal;

import java.util.Objects;

public final class SlackModalOpenResult {

    private final boolean opened;
    private final String error;
    private final String warning;

    private SlackModalOpenResult(boolean opened, String error, String warning) {
        this.opened = opened;
        this.error = error;
        this.warning = warning;
    }

    public static SlackModalOpenResult success(String warning) {
        return new SlackModalOpenResult(true, null, normalizeNullableText(warning, "warning"));
    }

    public static SlackModalOpenResult failure(String error, String warning) {
        return new SlackModalOpenResult(false, requireText(error, "error"), normalizeNullableText(warning, "warning"));
    }

    public boolean isOpened() {
        return opened;
    }

    public String getError() {
        return error;
    }

    public String getWarning() {
        return warning;
    }

    private static String requireText(String value, String fieldName) {
        Objects.requireNonNull(value, fieldName + " must not be null");
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw new SlackModalValidationException(fieldName + " must not be blank");
        }
        return trimmed;
    }

    private static String normalizeNullableText(String value, String fieldName) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw new SlackModalValidationException(fieldName + " must not be blank");
        }
        return trimmed;
    }
}
