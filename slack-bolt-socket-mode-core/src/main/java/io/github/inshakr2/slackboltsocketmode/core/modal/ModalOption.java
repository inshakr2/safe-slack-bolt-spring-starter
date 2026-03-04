package io.github.inshakr2.slackboltsocketmode.core.modal;

import com.slack.api.model.block.composition.OptionObject;

import static com.slack.api.model.block.composition.BlockCompositions.plainText;

public final class ModalOption {

    private static final int MAX_TEXT_LENGTH = 75;
    private static final int MAX_VALUE_LENGTH = 150;

    private final String text;
    private final String value;

    private ModalOption(String text, String value) {
        this.text = requireLength(text, "text", MAX_TEXT_LENGTH);
        this.value = requireLength(value, "value", MAX_VALUE_LENGTH);
    }

    public static ModalOption of(String text, String value) {
        return new ModalOption(text, value);
    }

    public String getText() {
        return text;
    }

    public String getValue() {
        return value;
    }

    OptionObject toSlackOption() {
        return OptionObject.builder()
                .text(plainText(text))
                .value(value)
                .build();
    }

    private static String requireLength(String value, String fieldName, int maxLength) {
        if (value == null) {
            throw SlackModalValidationException.nullField(fieldName);
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw SlackModalValidationException.blankField(fieldName);
        }
        if (trimmed.length() > maxLength) {
            throw SlackModalValidationException.lengthExceeded(fieldName, maxLength, trimmed.length());
        }
        return trimmed;
    }
}
