package io.github.inshakr2.slackboltsocketmode.core.experimental.modal;

import com.slack.api.model.block.DividerBlock;
import com.slack.api.model.block.HeaderBlock;
import com.slack.api.model.block.InputBlock;
import com.slack.api.model.block.LayoutBlock;
import com.slack.api.model.block.SectionBlock;
import com.slack.api.model.block.element.DatePickerElement;
import com.slack.api.model.block.element.PlainTextInputElement;
import com.slack.api.model.block.element.RadioButtonsElement;
import com.slack.api.model.block.element.StaticSelectElement;
import com.slack.api.model.block.element.TimePickerElement;
import com.slack.api.model.view.View;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SlackModalBuilderTest {

    @Test
    void buildsModalWithAllSupportedInputBlocks() {
        // Given
        ModalFieldKey<String> operatorKey = ModalFieldKey.singleSelect("operator");
        ModalFieldKey<?> dateKey = ModalFieldKey.date("visit_date");
        ModalFieldKey<?> timeKey = ModalFieldKey.time("visit_time");
        ModalFieldKey<String> delayReasonKey = ModalFieldKey.text("delay_reason");
        ModalFieldKey<String> actionTypeKey = ModalFieldKey.radio("action_type");

        // When
        View view = SlackModalBuilder.modal("requirement-confirm-submit", "확정 처리하기", "확인", "취소")
                .privateMetadata("requirement_id=101")
                .addHeader("요청 건을 확정 처리합니다.")
                .addStaticSelect(operatorKey, "담당자", "담당자를 선택하세요.", List.of(
                        ModalOption.of("Alice", "1001"),
                        ModalOption.of("Bob", "1002")
                ), false)
                .addDatePicker(dateKey, "확정일시 : 날짜", "날짜를 입력하세요.", false)
                .addTimePicker(timeKey, "확정일시 : 시간", "시간을 입력하세요.", false)
                .addTextInput(delayReasonKey, "확정지연사유", "확정지연사유를 입력하세요.", true, true)
                .addDivider()
                .addSection("대응구분")
                .addRadioButtons(actionTypeKey, "대응구분", List.of(
                        ModalOption.of("출동", "DISPATCH"),
                        ModalOption.of("원격", "REMOTE")
                ), false)
                .build();

        // Then
        assertThat(view.getType()).isEqualTo("modal");
        assertThat(view.getCallbackId()).isEqualTo("requirement-confirm-submit");
        assertThat(view.getPrivateMetadata()).isEqualTo("requirement_id=101");
        assertThat(view.getBlocks()).hasSize(8);

        LayoutBlock header = view.getBlocks().get(0);
        LayoutBlock staticSelect = view.getBlocks().get(1);
        LayoutBlock datePicker = view.getBlocks().get(2);
        LayoutBlock timePicker = view.getBlocks().get(3);
        LayoutBlock textInput = view.getBlocks().get(4);
        LayoutBlock divider = view.getBlocks().get(5);
        LayoutBlock section = view.getBlocks().get(6);
        LayoutBlock radioButtons = view.getBlocks().get(7);

        assertThat(header).isInstanceOf(HeaderBlock.class);
        assertThat(staticSelect).isInstanceOf(InputBlock.class);
        assertThat(((InputBlock) staticSelect).getElement()).isInstanceOf(StaticSelectElement.class);
        assertThat(((InputBlock) staticSelect).getBlockId()).isEqualTo("operator_input");
        assertThat(((InputBlock) staticSelect).isOptional()).isFalse();

        assertThat(datePicker).isInstanceOf(InputBlock.class);
        assertThat(((InputBlock) datePicker).getElement()).isInstanceOf(DatePickerElement.class);

        assertThat(timePicker).isInstanceOf(InputBlock.class);
        assertThat(((InputBlock) timePicker).getElement()).isInstanceOf(TimePickerElement.class);

        assertThat(textInput).isInstanceOf(InputBlock.class);
        assertThat(((InputBlock) textInput).getElement()).isInstanceOf(PlainTextInputElement.class);
        assertThat(((InputBlock) textInput).isOptional()).isTrue();
        assertThat(((PlainTextInputElement) ((InputBlock) textInput).getElement()).isMultiline()).isTrue();

        assertThat(divider).isInstanceOf(DividerBlock.class);
        assertThat(section).isInstanceOf(SectionBlock.class);
        assertThat(radioButtons).isInstanceOf(InputBlock.class);
        assertThat(((InputBlock) radioButtons).getElement()).isInstanceOf(RadioButtonsElement.class);
    }

    @Test
    void rejectsFieldTypeMismatch() {
        // Given
        ModalFieldKey<String> selectKey = ModalFieldKey.singleSelect("operator");

        // When & Then
        assertThatThrownBy(() -> SlackModalBuilder.modal("confirm-submit", "확정 처리", "확인", "취소")
                .addTextInput(selectKey, "담당자", "입력하세요", false, false))
                .isInstanceOf(SlackModalValidationException.class)
                .hasMessageContaining("field type mismatch");
    }

    @Test
    void rejectsEmptySelectOptions() {
        // Given
        ModalFieldKey<String> selectKey = ModalFieldKey.singleSelect("operator");

        // When & Then
        assertThatThrownBy(() -> SlackModalBuilder.modal("confirm-submit", "확정 처리", "확인", "취소")
                .addStaticSelect(selectKey, "담당자", "담당자를 선택하세요", List.of(), false))
                .isInstanceOf(SlackModalValidationException.class)
                .hasMessageContaining("options must not be empty");
    }

    @Test
    void rejectsCallbackIdLengthOverflow() {
        // Given
        String tooLongCallbackId = "a".repeat(256);

        // When & Then
        assertThatThrownBy(() -> SlackModalBuilder.modal(tooLongCallbackId, "확정 처리", "확인", "취소"))
                .isInstanceOf(SlackModalValidationException.class)
                .hasMessageContaining("callbackId length must be <= 255");
    }

    @Test
    void rejectsPrivateMetadataLengthOverflow() {
        // Given
        String tooLongMetadata = "a".repeat(3001);

        // When & Then
        assertThatThrownBy(() -> SlackModalBuilder.modal("confirm-submit", "확정 처리", "확인", "취소")
                .privateMetadata(tooLongMetadata))
                .isInstanceOf(SlackModalValidationException.class)
                .hasMessageContaining("privateMetadata length must be <= 3000");
    }
}
