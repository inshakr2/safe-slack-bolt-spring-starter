package io.github.inshakr2.slackboltsocketmode.core.modal;

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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.slack.api.model.block.Blocks.divider;
import static com.slack.api.model.block.Blocks.header;
import static com.slack.api.model.block.Blocks.input;
import static com.slack.api.model.block.Blocks.section;
import static com.slack.api.model.block.composition.BlockCompositions.plainText;

public final class SlackModalBuilder {

    private static final int MAX_ID_LENGTH = 255;
    private static final int MAX_PRIVATE_METADATA_LENGTH = 3000;
    private static final int MAX_MODAL_LABEL_LENGTH = 24;
    private static final int MAX_HEADER_TEXT_LENGTH = 150;
    private static final int MAX_SECTION_TEXT_LENGTH = 3000;
    private static final int MAX_INPUT_LABEL_LENGTH = 2000;
    private static final int MAX_PLACEHOLDER_LENGTH = 150;
    private static final int MAX_STATIC_SELECT_OPTION_COUNT = 100;
    private static final int MAX_RADIO_OPTION_COUNT = 10;
    private static final int MAX_BLOCK_COUNT = 100;

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
    // Tracks duplicated block_id and fails fast when same ID is reused.
    private final Set<String> usedBlockIds = new HashSet<>();
    // Tracks duplicated action_id and fails fast when same ID is reused.
    private final Set<String> usedActionIds = new HashSet<>();
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
        addBlock(header(h -> h.text(plainText(requireLength(text, "headerText", MAX_HEADER_TEXT_LENGTH)))));
        return this;
    }

    /**
     * Appends a divider block into {@code blocks}.
     */
    public SlackModalBuilder addDivider() {
        addBlock(divider());
        return this;
    }

    /**
     * Appends a section block into {@code blocks}.
     */
    public SlackModalBuilder addSection(String text) {
        addBlock(section(s -> s.text(plainText(requireLength(text, "sectionText", MAX_SECTION_TEXT_LENGTH)))));
        return this;
    }

    /**
     * Appends a plain_text_input InputBlock into {@code blocks}.
     * {@code key.blockId -> block_id, key.actionId -> action_id}.
     * label/placeholder/optional/multiline are mapped to the input element.
     */
    public SlackModalBuilder addTextInput(ModalFieldKey<String> key,
                                          String label,
                                          String placeholder,
                                          boolean optional,
                                          boolean multiline) {
        ensureFieldType(key, ModalFieldType.PLAIN_TEXT_INPUT);
        registerInputKey(key);
        addBlock(input(i -> i
                .blockId(key.getBlockId())
                .optional(optional)
                .element(PlainTextInputElement.builder()
                        .actionId(key.getActionId())
                        .placeholder(plainText(requireLength(placeholder, "placeholder", MAX_PLACEHOLDER_LENGTH)))
                        .multiline(multiline)
                        .build())
                .label(plainText(requireLength(label, "label", MAX_INPUT_LABEL_LENGTH)))));
        return this;
    }

    /**
     * Appends a datepicker InputBlock into {@code blocks}.
     * {@code key.blockId -> block_id, key.actionId -> action_id}.
     * label/placeholder/optional are mapped to the input element.
     */
    public SlackModalBuilder addDatePicker(ModalFieldKey<?> key,
                                           String label,
                                           String placeholder,
                                           boolean optional) {
        ensureFieldType(key, ModalFieldType.DATE_PICKER);
        registerInputKey(key);
        addBlock(input(i -> i
                .blockId(key.getBlockId())
                .optional(optional)
                .element(DatePickerElement.builder()
                        .actionId(key.getActionId())
                        .placeholder(plainText(requireLength(placeholder, "placeholder", MAX_PLACEHOLDER_LENGTH)))
                        .build())
                .label(plainText(requireLength(label, "label", MAX_INPUT_LABEL_LENGTH)))));
        return this;
    }

    /**
     * Appends a timepicker InputBlock into {@code blocks}.
     * {@code key.blockId -> block_id, key.actionId -> action_id}.
     * label/placeholder/optional are mapped to the input element.
     */
    public SlackModalBuilder addTimePicker(ModalFieldKey<?> key,
                                           String label,
                                           String placeholder,
                                           boolean optional) {
        ensureFieldType(key, ModalFieldType.TIME_PICKER);
        registerInputKey(key);
        addBlock(input(i -> i
                .blockId(key.getBlockId())
                .optional(optional)
                .element(TimePickerElement.builder()
                        .actionId(key.getActionId())
                        .placeholder(plainText(requireLength(placeholder, "placeholder", MAX_PLACEHOLDER_LENGTH)))
                        .build())
                .label(plainText(requireLength(label, "label", MAX_INPUT_LABEL_LENGTH)))));
        return this;
    }

    /**
     * Appends a static_select InputBlock into {@code blocks}.
     * {@code key.blockId -> block_id, key.actionId -> action_id}.
     * label/placeholder/options/optional are mapped to the input element.
     */
    public SlackModalBuilder addStaticSelect(ModalFieldKey<String> key,
                                             String label,
                                             String placeholder,
                                             List<ModalOption> options,
                                             boolean optional) {
        ensureFieldType(key, ModalFieldType.STATIC_SELECT);
        registerInputKey(key);
        addBlock(input(i -> i
                .blockId(key.getBlockId())
                .optional(optional)
                .element(StaticSelectElement.builder()
                        .actionId(key.getActionId())
                        .placeholder(plainText(requireLength(placeholder, "placeholder", MAX_PLACEHOLDER_LENGTH)))
                        .options(toOptionObjects(options, MAX_STATIC_SELECT_OPTION_COUNT))
                        .build())
                .label(plainText(requireLength(label, "label", MAX_INPUT_LABEL_LENGTH)))));
        return this;
    }

    /**
     * Appends a radio_buttons InputBlock into {@code blocks}.
     * {@code key.blockId -> block_id, key.actionId -> action_id}.
     * label/options/optional are mapped to the input element.
     */
    public SlackModalBuilder addRadioButtons(ModalFieldKey<String> key,
                                             String label,
                                             List<ModalOption> options,
                                             boolean optional) {
        ensureFieldType(key, ModalFieldType.RADIO_BUTTONS);
        registerInputKey(key);
        addBlock(input(i -> i
                .blockId(key.getBlockId())
                .optional(optional)
                .element(RadioButtonsElement.builder()
                        .actionId(key.getActionId())
                        .options(toOptionObjects(options, MAX_RADIO_OPTION_COUNT))
                        .build())
                .label(plainText(requireLength(label, "label", MAX_INPUT_LABEL_LENGTH)))));
        return this;
    }

    /**
     * Builds final Slack View by combining base fields
     * (callbackId/title/submitText/closeText/privateMetadata) and accumulated {@code blocks}.
     */
    public View build() {
        if (blocks.isEmpty()) {
            throw SlackModalValidationException.invalidValue("blocks", "must not be empty");
        }
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
        if (value == null) {
            throw SlackModalValidationException.nullField(fieldName);
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw SlackModalValidationException.blankField(fieldName);
        }
        return trimmed;
    }

    /**
     * Applies max length policy after base text validation.
     */
    private static String requireLength(String value, String fieldName, int maxLength) {
        String trimmed = requireText(value, fieldName);
        if (trimmed.length() > maxLength) {
            throw SlackModalValidationException.lengthExceeded(fieldName, maxLength, trimmed.length());
        }
        return trimmed;
    }

    /**
     * Guards against wrong key type usage in add* methods.
     */
    private static void ensureFieldType(ModalFieldKey<?> key, ModalFieldType expectedType) {
        if (key == null) {
            throw SlackModalValidationException.nullField("key");
        }
        if (key.getFieldType() != expectedType) {
            throw SlackModalValidationException.invalidValue(
                    "key.fieldType",
                    "field type mismatch: expected " + expectedType + ", but was " + key.getFieldType()
            );
        }
    }

    /**
     * Converts internal option model to Slack OptionObject list for select/radio elements.
     */
    private static List<OptionObject> toOptionObjects(List<ModalOption> options, int maxOptions) {
        if (options == null) {
            throw SlackModalValidationException.nullField("options");
        }
        if (options.isEmpty()) {
            throw SlackModalValidationException.invalidValue("options", "must not be empty");
        }
        if (options.size() > maxOptions) {
            throw SlackModalValidationException.invalidValue(
                    "options",
                    "size must be <= " + maxOptions + ", actual=" + options.size()
            );
        }
        List<OptionObject> result = new ArrayList<>(options.size());
        for (ModalOption option : options) {
            if (option == null) {
                throw SlackModalValidationException.invalidValue("options", "must not contain null item");
            }
            result.add(option.toSlackOption());
        }
        return result;
    }

    private void registerInputKey(ModalFieldKey<?> key) {
        if (!usedBlockIds.add(key.getBlockId())) {
            throw SlackModalValidationException.invalidValue("blockId", "duplicate value: " + key.getBlockId());
        }
        if (!usedActionIds.add(key.getActionId())) {
            throw SlackModalValidationException.invalidValue("actionId", "duplicate value: " + key.getActionId());
        }
    }

    private void addBlock(LayoutBlock block) {
        if (blocks.size() >= MAX_BLOCK_COUNT) {
            throw SlackModalValidationException.invalidValue(
                    "blocks",
                    "size must be <= " + MAX_BLOCK_COUNT + ", actual=" + (blocks.size() + 1)
            );
        }
        blocks.add(block);
    }
}
