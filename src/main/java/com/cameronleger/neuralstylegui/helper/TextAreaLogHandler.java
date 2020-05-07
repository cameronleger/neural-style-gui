package com.cameronleger.neuralstylegui.helper;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.logging.LogRecord;
import java.util.logging.StreamHandler;

public class TextAreaLogHandler extends StreamHandler {
    private TextArea textArea = null;
    private final DateTimeFormatter formatter =
            DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT, FormatStyle.MEDIUM)
                    .withZone(ZoneId.systemDefault());

    public TextAreaLogHandler(TextArea textArea) {
        this.textArea = textArea;
    }

    @Override
    public void publish(LogRecord record) {
        super.publish(record);
        if (textArea != null) {
            Platform.runLater(() -> textArea.appendText(String.format(
                    "[%s] %s\n",
                    formatter.format(record.getInstant()),
                    record.getMessage()
            )));
        }
    }
}
