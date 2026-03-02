package io.github.inshakr2.slackboltsocketmode.core.experimental.modal;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ModalFieldKeyTest {

    @Test
    void createsDerivedBlockIdAndActionId() {
        // Given
        String keyName = "operator";

        // When
        ModalFieldKey<String> fieldKey = ModalFieldKey.text(keyName);

        // Then
        assertThat(fieldKey.getName()).isEqualTo("operator");
        assertThat(fieldKey.getFieldType()).isEqualTo(ModalFieldType.PLAIN_TEXT_INPUT);
        assertThat(fieldKey.getBlockId()).isEqualTo("operator_input");
        assertThat(fieldKey.getActionId()).isEqualTo("operator_action");
    }

    @Test
    void supportsFactoryMethodPerFieldType() {
        // Given
        String keyName = "visit_date";

        // When
        ModalFieldKey<?> dateKey = ModalFieldKey.date(keyName);
        ModalFieldKey<?> timeKey = ModalFieldKey.time("visit_time");
        ModalFieldKey<?> selectKey = ModalFieldKey.singleSelect("operator");
        ModalFieldKey<?> radioKey = ModalFieldKey.radio("action_type");

        // Then
        assertThat(dateKey.getFieldType()).isEqualTo(ModalFieldType.DATE_PICKER);
        assertThat(timeKey.getFieldType()).isEqualTo(ModalFieldType.TIME_PICKER);
        assertThat(selectKey.getFieldType()).isEqualTo(ModalFieldType.STATIC_SELECT);
        assertThat(radioKey.getFieldType()).isEqualTo(ModalFieldType.RADIO_BUTTONS);
    }

    @Test
    void rejectsInvalidFieldKeyPattern() {
        // Given
        String invalid = "Operator-Id";

        // When & Then
        assertThatThrownBy(() -> ModalFieldKey.text(invalid))
                .isInstanceOf(SlackModalValidationException.class)
                .hasMessageContaining("must match pattern");
    }

    @Test
    void rejectsBlankFieldKeyName() {
        // Given
        String blank = "   ";

        // When & Then
        assertThatThrownBy(() -> ModalFieldKey.text(blank))
                .isInstanceOf(SlackModalValidationException.class)
                .hasMessageContaining("must not be blank");
    }

    @Test
    void rejectsWhenDerivedIdsExceedMaximumLength() {
        // Given
        String tooLongName = "a".repeat(250);

        // When & Then
        assertThatThrownBy(() -> ModalFieldKey.text(tooLongName))
                .isInstanceOf(SlackModalValidationException.class)
                .hasMessageContaining("length must be <=");
    }
}
