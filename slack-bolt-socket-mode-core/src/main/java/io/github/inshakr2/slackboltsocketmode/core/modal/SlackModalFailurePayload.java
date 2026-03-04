package io.github.inshakr2.slackboltsocketmode.core.modal;

public final class SlackModalFailurePayload {

    private final String code;
    private final String message;
    private final String error;
    private final String warning;

    private SlackModalFailurePayload(String code, String message, String error, String warning) {
        this.code = code;
        this.message = message;
        this.error = error;
        this.warning = warning;
    }

    public static SlackModalFailurePayload of(SlackModalFailureInfo failureInfo, SlackModalOpenResult openResult) {
        if (failureInfo == null) {
            throw SlackModalValidationException.nullField("failureInfo");
        }
        if (openResult == null) {
            throw SlackModalValidationException.nullField("openResult");
        }
        return new SlackModalFailurePayload(
                failureInfo.getCode(),
                failureInfo.getMessage(),
                openResult.getError(),
                openResult.getWarning()
        );
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getError() {
        return error;
    }

    public String getWarning() {
        return warning;
    }
}
