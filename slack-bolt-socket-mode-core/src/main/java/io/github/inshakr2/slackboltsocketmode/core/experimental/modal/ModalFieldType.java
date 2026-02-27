package io.github.inshakr2.slackboltsocketmode.core.experimental.modal;

public enum ModalFieldType {

    PLAIN_TEXT_INPUT("plain_text_input"),
    DATE_PICKER("datepicker"),
    TIME_PICKER("timepicker"),
    STATIC_SELECT("static_select"),
    RADIO_BUTTONS("radio_buttons");

    private final String slackElementType;

    ModalFieldType(String slackElementType) {
        this.slackElementType = slackElementType;
    }

    public String getSlackElementType() {
        return slackElementType;
    }
}
