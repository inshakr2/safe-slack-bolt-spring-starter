package io.github.inshakr2.slackboltsocketmode.core.modal;

public final class SlackModalFailureInfo {

    private static final String DEFAULT_CODE = "SLACK_MODAL_OPEN_FAILED";
    private static final String DEFAULT_MESSAGE = "Failed to open modal";
    private static final SlackModalFailureInfo DEFAULT = new SlackModalFailureInfo(DEFAULT_CODE, DEFAULT_MESSAGE);

    private final String code;
    private final String message;

    private SlackModalFailureInfo(String code, String message) {
        this.code = requireText(code, "code");
        this.message = requireText(message, "message");
    }

    public static SlackModalFailureInfo of(String code, String message) {
        return new SlackModalFailureInfo(code, message);
    }

    public static SlackModalFailureInfo defaultInfo() {
        return DEFAULT;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    private static String requireText(String value, String fieldName) {
        if (value == null) {
            throw SlackModalValidationException.nullField(fieldName);
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw SlackModalValidationException.blankField(fieldName);
        }
        return trimmed;
    }
}
