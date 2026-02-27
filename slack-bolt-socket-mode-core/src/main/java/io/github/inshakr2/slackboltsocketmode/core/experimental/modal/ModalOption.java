package io.github.inshakr2.slackboltsocketmode.core.experimental.modal;

import com.slack.api.model.block.composition.OptionObject;

import java.util.Objects;

import static com.slack.api.model.block.composition.BlockCompositions.plainText;

public final class ModalOption {

    private final String text;
    private final String value;

    private ModalOption(String text, String value) {
        this.text = requireText(text, "text");
        this.value = requireText(value, "value");
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

    private static String requireText(String value, String fieldName) {
        Objects.requireNonNull(value, fieldName + " must not be null");
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw new SlackModalValidationException(fieldName + " must not be blank");
        }
        return trimmed;
    }
}
