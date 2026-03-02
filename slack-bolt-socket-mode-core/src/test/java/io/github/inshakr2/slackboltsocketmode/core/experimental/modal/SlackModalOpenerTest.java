package io.github.inshakr2.slackboltsocketmode.core.experimental.modal;

import com.slack.api.bolt.context.Context;
import com.slack.api.bolt.response.Response;
import com.slack.api.RequestConfigurator;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.request.views.ViewsOpenRequest;
import com.slack.api.methods.response.views.ViewsOpenResponse;
import com.slack.api.model.view.View;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SlackModalOpenerTest {

    @Test
    void openReturnsTrueWhenViewsOpenIsOk() throws Exception {
        // Given
        Context context = mock(Context.class);
        MethodsClient methodsClient = mock(MethodsClient.class);
        ViewsOpenResponse viewsOpenResponse = mock(ViewsOpenResponse.class);
        when(context.client()).thenReturn(methodsClient);
        when(methodsClient.viewsOpen(anyConfigurator())).thenReturn(viewsOpenResponse);
        when(viewsOpenResponse.isOk()).thenReturn(true);

        // When
        boolean opened = SlackModalOpener.open(context, "trigger-1", View.builder().type("modal").build());

        // Then
        assertThat(opened).isTrue();
    }

    @Test
    void openOrAckReturnsAckWhenModalOpened() throws Exception {
        // Given
        Context context = mock(Context.class);
        MethodsClient methodsClient = mock(MethodsClient.class);
        ViewsOpenResponse viewsOpenResponse = mock(ViewsOpenResponse.class);
        Response ackResponse = Response.builder().statusCode(200).body("ack").build();
        when(context.client()).thenReturn(methodsClient);
        when(methodsClient.viewsOpen(anyConfigurator())).thenReturn(viewsOpenResponse);
        when(viewsOpenResponse.isOk()).thenReturn(true);
        when(context.ack()).thenReturn(ackResponse);

        // When
        Response result = SlackModalOpener.openOrAck(context, "trigger-1", View.builder().type("modal").build());

        // Then
        assertThat(result).isSameAs(ackResponse);
        verify(context).ack();
    }

    @Test
    void openOrAckReturnsAckWithJsonWhenModalOpenFails() throws Exception {
        // Given
        Context context = mock(Context.class);
        MethodsClient methodsClient = mock(MethodsClient.class);
        ViewsOpenResponse viewsOpenResponse = mock(ViewsOpenResponse.class);
        Response ackWithJsonResponse = Response.builder().statusCode(200).body("ack-json").build();
        ArgumentCaptor<Object> responseCaptor = ArgumentCaptor.forClass(Object.class);

        when(context.client()).thenReturn(methodsClient);
        when(methodsClient.viewsOpen(anyConfigurator())).thenReturn(viewsOpenResponse);
        when(viewsOpenResponse.isOk()).thenReturn(false);
        when(context.ackWithJson(responseCaptor.capture())).thenReturn(ackWithJsonResponse);

        // When
        Response result = SlackModalOpener.openOrAck(context, "trigger-1", View.builder().type("modal").build());

        // Then
        assertThat(result).isSameAs(ackWithJsonResponse);
        assertThat(responseCaptor.getValue()).isInstanceOf(Response.class);
        Response failureResponse = (Response) responseCaptor.getValue();
        assertThat(failureResponse.getStatusCode()).isEqualTo(500);
        assertThat(failureResponse.getBody()).isEqualTo("Failed to open modal");
    }

    @Test
    void failureResponseRejectsInvalidStatusCode() {
        // Given
        int invalidStatusCode = 99;

        // When & Then
        assertThatThrownBy(() -> SlackModalOpener.failureResponse(invalidStatusCode, "failed"))
                .isInstanceOf(SlackModalValidationException.class)
                .hasMessageContaining("statusCode must be between 100 and 599");
    }

    @SuppressWarnings("unchecked")
    private static RequestConfigurator<ViewsOpenRequest.ViewsOpenRequestBuilder> anyConfigurator() {
        return (RequestConfigurator<ViewsOpenRequest.ViewsOpenRequestBuilder>) any(RequestConfigurator.class);
    }
}
