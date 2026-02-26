package io.github.inshakr2.safeslackbolt.core.handler.view;

import com.slack.api.bolt.context.builtin.ViewSubmissionContext;
import com.slack.api.bolt.response.Response;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ViewSubmissionValidationErrorsTest {

    @Test
    void storesAndExposesValidationErrors() {
        // Given
        ViewSubmissionValidationErrors errors = ViewSubmissionValidationErrors.of("agenda", "Agenda is required.")
                .add("owner", "Owner is required.");

        // When
        var values = errors.asMap();

        // Then
        assertThat(values)
                .containsEntry("agenda", "Agenda is required.")
                .containsEntry("owner", "Owner is required.");
    }

    @Test
    void ackCallsContextAckWithErrors() {
        // Given
        ViewSubmissionContext context = mock(ViewSubmissionContext.class);
        Response expected = Response.builder().statusCode(200).build();
        ViewSubmissionValidationErrors errors = ViewSubmissionValidationErrors.of("agenda", "Agenda is required.");
        when(context.ackWithErrors(errors.asMap())).thenReturn(expected);

        // When
        Response result = errors.ack(context);

        // Then
        verify(context).ackWithErrors(errors.asMap());
        assertThat(result).isEqualTo(expected);
    }
}
