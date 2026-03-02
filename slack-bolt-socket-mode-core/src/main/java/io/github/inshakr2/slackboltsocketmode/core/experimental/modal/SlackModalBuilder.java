package io.github.inshakr2.slackboltsocketmode.core.experimental.modal;

import com.slack.api.model.block.InputBlock;
import com.slack.api.model.block.LayoutBlock;
import com.slack.api.model.block.composition.OptionObject;
import com.slack.api.model.block.element.DatePickerElement;
import com.slack.api.model.block.element.PlainTextInputElement;
import com.slack.api.model.block.element.RadioButtonsElement;
import com.slack.api.model.block.element.StaticSelectElement;
import com.slack.api.model.block.element.TimePickerElement;
import com.slack.api.model.view.View;
import com.slack.api.model.view.ViewClose;
import com.slack.api.model.view.ViewSubmit;
import com.slack.api.model.view.ViewTitle;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.slack.api.model.block.Blocks.divider;
import static com.slack.api.model.block.Blocks.header;
import static com.slack.api.model.block.Blocks.input;
import static com.slack.api.model.block.Blocks.section;
import static com.slack.api.model.block.composition.BlockCompositions.plainText;

public final class SlackModalBuilder {

    private static final int MAX_ID_LENGTH = 255;
    private static final int MAX_PRIVATE_METADATA_LENGTH = 3000;
    private static final int MAX_MODAL_LABEL_LENGTH = 24;

    // Slack view callback_id used when the modal is submitted.
    private final String callbackId;
    // Top title shown in the modal header.
    private final String title;
    // Submit button label.
    private final String submitText;
    // Close button label.
    private final String closeText;
    // Ordered block list accumulated by add* methods.
    private final List<LayoutBlock> blocks = new ArrayList<>();
    // Optional private_metadata passed through submission payload.
    private String privateMetadata;

    /**
     * Initializes the immutable modal header fields used by {@link #build()}.
     */
    private SlackModalBuilder(String callbackId, String title, String submitText, String closeText) {
        this.callbackId = requireLength(callbackId, "callbackId", MAX_ID_LENGTH);
        this.title = requireLength(title, "title", MAX_MODAL_LABEL_LENGTH);
        this.submitText = requireLength(submitText, "submitText", MAX_MODAL_LABEL_LENGTH);
        this.closeText = requireLength(closeText, "closeText", MAX_MODAL_LABEL_LENGTH);
    }

    /**
     * Factory method for a fluent builder.
     * All arguments become base modal fields consumed in {@link #build()}.
     */
    public static SlackModalBuilder modal(String callbackId, String title, String submitText, String closeText) {
        return new SlackModalBuilder(callbackId, title, submitText, closeText);
    }

    /**
     * Sets private_metadata field that is later written to the final View in {@link #build()}.
     */
    public SlackModalBuilder privateMetadata(String privateMetadata) {
        this.privateMetadata = requireLength(privateMetadata, "privateMetadata", MAX_PRIVATE_METADATA_LENGTH);
        return this;
    }

    /**
     * Appends a header block into {@code blocks}.
     */
    public SlackModalBuilder addHeader(String text) {
        blocks.add(header(h -> h.text(plainText(requireText(text, "text")))));
        return this;
    }

    /**
     * Appends a divider block into {@code blocks}.
     */
    public SlackModalBuilder addDivider() {
        blocks.add(divider());
        return this;
    }

    /**
     * Appends a section block into {@code blocks}.
     */
    public SlackModalBuilder addSection(String text) {
        blocks.add(section(s -> s.text(plainText(requireText(text, "text")))));
        return this;
    }

    /**
     * Appends a plain_text_input InputBlock into {@code blocks}.
     * key.blockId -> block_id, key.actionId -> action_id.
     * label/placeholder/optional/multiline are mapped to the input element.
     */
    public SlackModalBuilder addTextInput(ModalFieldKey<String> key,
                                          String label,
                                          String placeholder,
                                          boolean optional,
                                          boolean multiline) {
        ensureFieldType(key, ModalFieldType.PLAIN_TEXT_INPUT);
        blocks.add(input(i -> i
                .blockId(key.getBlockId())
                .optional(optional)
                .element(PlainTextInputElement.builder()
                        .actionId(key.getActionId())
                        .placeholder(plainText(requireText(placeholder, "placeholder")))
                        .multiline(multiline)
                        .build())
                .label(plainText(requireText(label, "label")))));
        return this;
    }

    /**
     * Appends a datepicker InputBlock into {@code blocks}.
     * key.blockId -> block_id, key.actionId -> action_id.
     * label/placeholder/optional are mapped to the input element.
     */
    public SlackModalBuilder addDatePicker(ModalFieldKey<?> key,
                                           String label,
                                           String placeholder,
                                           boolean optional) {
        ensureFieldType(key, ModalFieldType.DATE_PICKER);
        blocks.add(input(i -> i
                .blockId(key.getBlockId())
                .optional(optional)
                .element(DatePickerElement.builder()
                        .actionId(key.getActionId())
                        .placeholder(plainText(requireText(placeholder, "placeholder")))
                        .build())
                .label(plainText(requireText(label, "label")))));
        return this;
    }

    /**
     * Appends a timepicker InputBlock into {@code blocks}.
     * key.blockId -> block_id, key.actionId -> action_id.
     * label/placeholder/optional are mapped to the input element.
     */
    public SlackModalBuilder addTimePicker(ModalFieldKey<?> key,
                                           String label,
                                           String placeholder,
                                           boolean optional) {
        ensureFieldType(key, ModalFieldType.TIME_PICKER);
        blocks.add(input(i -> i
                .blockId(key.getBlockId())
                .optional(optional)
                .element(TimePickerElement.builder()
                        .actionId(key.getActionId())
                        .placeholder(plainText(requireText(placeholder, "placeholder")))
                        .build())
                .label(plainText(requireText(label, "label")))));
        return this;
    }

    /**
     * Appends a static_select InputBlock into {@code blocks}.
     * key.blockId -> block_id, key.actionId -> action_id.
     * label/placeholder/options/optional are mapped to the input element.
     */
    public SlackModalBuilder addStaticSelect(ModalFieldKey<String> key,
                                             String label,
                                             String placeholder,
                                             List<ModalOption> options,
                                             boolean optional) {
        ensureFieldType(key, ModalFieldType.STATIC_SELECT);
        blocks.add(input(i -> i
                .blockId(key.getBlockId())
                .optional(optional)
                .element(StaticSelectElement.builder()
                        .actionId(key.getActionId())
                        .placeholder(plainText(requireText(placeholder, "placeholder")))
                        .options(toOptionObjects(options))
                        .build())
                .label(plainText(requireText(label, "label")))));
        return this;
    }

    /**
     * Appends a radio_buttons InputBlock into {@code blocks}.
     * key.blockId -> block_id, key.actionId -> action_id.
     * label/options/optional are mapped to the input element.
     */
    public SlackModalBuilder addRadioButtons(ModalFieldKey<String> key,
                                             String label,
                                             List<ModalOption> options,
                                             boolean optional) {
        ensureFieldType(key, ModalFieldType.RADIO_BUTTONS);
        blocks.add(input(i -> i
                .blockId(key.getBlockId())
                .optional(optional)
                .element(RadioButtonsElement.builder()
                        .actionId(key.getActionId())
                        .options(toOptionObjects(options))
                        .build())
                .label(plainText(requireText(label, "label")))));
        return this;
    }

    /**
     * Builds final Slack View by combining base fields
     * (callbackId/title/submitText/closeText/privateMetadata) and accumulated {@code blocks}.
     */
    public View build() {
        View.ViewBuilder builder = View.builder()
                .type("modal")
                .callbackId(callbackId)
                .title(ViewTitle.builder().type("plain_text").text(title).build())
                .submit(ViewSubmit.builder().type("plain_text").text(submitText).build())
                .close(ViewClose.builder().type("plain_text").text(closeText).build())
                .blocks(new ArrayList<>(blocks));
        if (privateMetadata != null) {
            builder.privateMetadata(privateMetadata);
        }
        return builder.build();
    }

    /**
     * Validates non-null and non-blank text inputs used by all builder methods.
     */
    private static String requireText(String value, String fieldName) {
        Objects.requireNonNull(value, fieldName + " must not be null");
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw new SlackModalValidationException(fieldName + " must not be blank");
        }
        return trimmed;
    }

    /**
     * Applies max length policy after base text validation.
     */
    private static String requireLength(String value, String fieldName, int maxLength) {
        String trimmed = requireText(value, fieldName);
        if (trimmed.length() > maxLength) {
            throw new SlackModalValidationException(fieldName + " length must be <= " + maxLength);
        }
        return trimmed;
    }

    /**
     * Guards against wrong key type usage in add* methods.
     */
    private static void ensureFieldType(ModalFieldKey<?> key, ModalFieldType expectedType) {
        Objects.requireNonNull(key, "key must not be null");
        if (key.getFieldType() != expectedType) {
            throw new SlackModalValidationException("field type mismatch: expected " + expectedType
                    + ", but was " + key.getFieldType());
        }
    }

    /**
     * Converts internal option model to Slack OptionObject list for select/radio elements.
     */
    private static List<OptionObject> toOptionObjects(List<ModalOption> options) {
        Objects.requireNonNull(options, "options must not be null");
        if (options.isEmpty()) {
            throw new SlackModalValidationException("options must not be empty");
        }
        return options.stream()
                .map(Objects::requireNonNull)
                .map(ModalOption::toSlackOption)
                .collect(Collectors.toList());
    }
}
