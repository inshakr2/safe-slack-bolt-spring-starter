package io.github.inshakr2.slackboltsocketmode.core.experimental.modal;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;
import java.util.regex.Pattern;

public final class ModalFieldKey<T> {

    private static final int MAX_ID_LENGTH = 255;
    private static final String BLOCK_ID_SUFFIX = "_input";
    private static final String ACTION_ID_SUFFIX = "_action";
    private static final Pattern KEY_PATTERN = Pattern.compile("^[a-z0-9][a-z0-9_]*$");

    private final String name;
    private final ModalFieldType fieldType;
    private final String blockId;
    private final String actionId;

    private ModalFieldKey(String name, ModalFieldType fieldType) {
        String normalized = validateName(name);
        this.name = normalized;
        this.fieldType = Objects.requireNonNull(fieldType, "fieldType must not be null");
        this.blockId = validateIdLength(normalized + BLOCK_ID_SUFFIX, "blockId");
        this.actionId = validateIdLength(normalized + ACTION_ID_SUFFIX, "actionId");
    }

    public static ModalFieldKey<String> text(String name) {
        return new ModalFieldKey<>(name, ModalFieldType.PLAIN_TEXT_INPUT);
    }

    public static ModalFieldKey<LocalDate> date(String name) {
        return new ModalFieldKey<>(name, ModalFieldType.DATE_PICKER);
    }

    public static ModalFieldKey<LocalTime> time(String name) {
        return new ModalFieldKey<>(name, ModalFieldType.TIME_PICKER);
    }

    public static ModalFieldKey<String> singleSelect(String name) {
        return new ModalFieldKey<>(name, ModalFieldType.STATIC_SELECT);
    }

    public static ModalFieldKey<String> radio(String name) {
        return new ModalFieldKey<>(name, ModalFieldType.RADIO_BUTTONS);
    }

    public String getName() {
        return name;
    }

    public ModalFieldType getFieldType() {
        return fieldType;
    }

    public String getBlockId() {
        return blockId;
    }

    public String getActionId() {
        return actionId;
    }

    private static String validateName(String name) {
        Objects.requireNonNull(name, "name must not be null");
        String normalized = name.trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        if (!KEY_PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException("name must match pattern: " + KEY_PATTERN.pattern());
        }
        return normalized;
    }

    private static String validateIdLength(String value, String fieldName) {
        if (value.length() > MAX_ID_LENGTH) {
            throw new IllegalArgumentException(fieldName + " length must be <= " + MAX_ID_LENGTH);
        }
        return value;
    }
}
