package com.cameronleger.neuralstylegui.helper;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.util.logging.LogRecord;
import java.util.logging.StreamHandler;

public class TextAreaLogHandler extends StreamHandler {
    private TextArea textArea = null;

    public TextAreaLogHandler(TextArea textArea) {
        this.textArea = textArea;
    }

    @Override
    public void publish(LogRecord record) {
        super.publish(record);
        if (textArea != null) {
            Platform.runLater(() -> textArea.appendText(String.format("%s\n", record.getMessage())));
        }
    }
}