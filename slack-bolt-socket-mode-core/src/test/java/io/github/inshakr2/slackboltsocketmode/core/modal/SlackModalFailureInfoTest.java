package io.github.inshakr2.slackboltsocketmode.core.modal;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SlackModalFailureInfoTest {

    @Test
    void createsFailureInfoWithCodeAndMessage() {
        // Given
        String code = "CUSTOM_CODE";
        String message = "Custom message";

        // When
        SlackModalFailureInfo info = SlackModalFailureInfo.of(code, message);

        // Then
        assertThat(info.getCode()).isEqualTo(code);
        assertThat(info.getMessage()).isEqualTo(message);
    }

    @Test
    void providesDefaultFailureInfo() {
        // Given

        // When
        SlackModalFailureInfo info = SlackModalFailureInfo.defaultInfo();

        // Then
        assertThat(info.getCode()).isEqualTo("SLACK_MODAL_OPEN_FAILED");
        assertThat(info.getMessage()).isEqualTo("Failed to open modal");
    }

    @Test
    void rejectsBlankCode() {
        // Given

        // When & Then
        assertThatThrownBy(() -> SlackModalFailureInfo.of("   ", "message"))
                .isInstanceOf(SlackModalValidationException.class)
                .hasMessageContaining("field=code");
    }

    @Test
    void rejectsNullMessage() {
        // Given

        // When & Then
        assertThatThrownBy(() -> SlackModalFailureInfo.of("CODE", null))
                .isInstanceOf(SlackModalValidationException.class)
                .hasMessageContaining("field=message");
    }
}
