package io.github.inshakr2.slackboltsocketmode.core.modal;

public class SlackModalValidationException extends RuntimeException {

    private static final String PREFIX = "[SLACK_MODAL_VALIDATION]";

    public SlackModalValidationException(String message) {
        super(message);
    }

    public static SlackModalValidationException nullField(String fieldName) {
        return new SlackModalValidationException(format(fieldName, "must not be null"));
    }

    public static SlackModalValidationException blankField(String fieldName) {
        return new SlackModalValidationException(format(fieldName, "must not be blank"));
    }

    public static SlackModalValidationException lengthExceeded(String fieldName, int maxLength, int actualLength) {
        return new SlackModalValidationException(
                format(fieldName, "length must be <= " + maxLength + ", actual=" + actualLength)
        );
    }

    public static SlackModalValidationException patternMismatch(String fieldName, String pattern, String value) {
        return new SlackModalValidationException(
                format(fieldName, "must match pattern: " + pattern + ", value=" + value)
        );
    }

    public static SlackModalValidationException invalidValue(String fieldName, String reason) {
        return new SlackModalValidationException(format(fieldName, reason));
    }

    private static String format(String fieldName, String reason) {
        return PREFIX + " " + reason + " (field=" + fieldName + ")";
    }
}
