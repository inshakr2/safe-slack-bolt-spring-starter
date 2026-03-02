package io.github.inshakr2.slackboltsocketmode.sample.handler;

import com.slack.api.bolt.context.builtin.GlobalShortcutContext;
import com.slack.api.bolt.request.builtin.GlobalShortcutRequest;
import com.slack.api.bolt.response.Response;
import com.slack.api.model.view.View;
import io.github.inshakr2.slackboltsocketmode.core.experimental.modal.ModalFieldKey;
import io.github.inshakr2.slackboltsocketmode.core.experimental.modal.ModalOption;
import io.github.inshakr2.slackboltsocketmode.core.experimental.modal.SlackModalBuilder;
import io.github.inshakr2.slackboltsocketmode.core.experimental.modal.SlackModalOpener;
import io.github.inshakr2.slackboltsocketmode.core.handler.shortcut.AbstractGlobalShortcutHandler;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SampleGlobalShortcutHandler extends AbstractGlobalShortcutHandler {

    private static final ModalFieldKey<String> OWNER_KEY = ModalFieldKey.singleSelect("owner");
    private static final ModalFieldKey<?> TARGET_DATE_KEY = ModalFieldKey.date("target_date");
    private static final ModalFieldKey<?> TARGET_TIME_KEY = ModalFieldKey.time("target_time");
    private static final ModalFieldKey<String> AGENDA_KEY = ModalFieldKey.text("agenda");
    private static final ModalFieldKey<String> ACTION_TYPE_KEY = ModalFieldKey.radio("action_type");

    @Override
    protected String getCallbackId() {
        return "sample-global-shortcut";
    }

    @Override
    protected Response handle(GlobalShortcutRequest req, GlobalShortcutContext ctx) throws Exception {
        View modal = SlackModalBuilder.modal(
                        "socket-mode-view-submit",
                        "Sample Modal",
                        "Submit",
                        "Cancel")
                .privateMetadata("source=global_shortcut")
                .addHeader("Schedule a follow-up action")
                .addStaticSelect(
                        OWNER_KEY,
                        "Owner",
                        "Select owner",
                        List.of(
                                ModalOption.of("Operator A", "1001"),
                                ModalOption.of("Operator B", "1002")
                        ),
                        false
                )
                .addDatePicker(TARGET_DATE_KEY, "Target date", "Pick a date", false)
                .addTimePicker(TARGET_TIME_KEY, "Target time", "Pick a time", false)
                .addTextInput(AGENDA_KEY, "Agenda", "Describe the agenda", false, true)
                .addDivider()
                .addSection("Action type")
                .addRadioButtons(
                        ACTION_TYPE_KEY,
                        "Action type",
                        List.of(
                                ModalOption.of("Dispatch", "DISPATCH"),
                                ModalOption.of("Remote", "REMOTE")
                        ),
                        false
                )
                .build();

        return SlackModalOpener.openOrAck(
                ctx,
                req.getPayload().getTriggerId(),
                modal,
                500,
                "Failed to open modal"
        );
    }
}
