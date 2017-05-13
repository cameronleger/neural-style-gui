package com.cameronleger.neuralstylegui.model;

import com.cameronleger.neuralstyle.FileUtils;
import javafx.beans.property.*;
import org.apache.commons.io.FilenameUtils;

import java.io.File;

public final class NeuralQueue {
    public final static int QUEUED_STYLE = 1;
    public final static int QUEUED_IMAGE = 2;

    public static NeuralQueueItem createQueueItem(File file) {
        if (file == null)
            return new NeuralQueueFakeItem(null);

        switch (FilenameUtils.getExtension(file.getAbsolutePath()).toLowerCase()) {
            case "png":
                return new NeuralQueueImageItem(file);
            case "json":
                return new NeuralQueueStyleItem(file);
            default:
                return new NeuralQueueFakeItem(null);
        }
    }

    public interface NeuralQueueActionCallback {
        void actionCallback();
    }

    public abstract static class NeuralQueueItem {
        int type = -1;
        ObjectProperty<File> file = new SimpleObjectProperty<>();
        StringProperty name = new SimpleStringProperty("");
        StringProperty status = new SimpleStringProperty("");
        String actionText = "";
        NeuralQueueActionCallback actionCallback;

        NeuralQueueItem(File file) {
            if (file != null) {
                this.file.setValue(file);
                this.name.setValue(file.getName());
            }
        }

        public int getType() {
            return type;
        }

        public File getFile() {
            return file.getValue();
        }
        public String getName() {
            return name.getValue();
        }
        public String getStatus() {
            return status.getValue();
        }
        public String getActionText() {
            return actionText;
        }

        public void setActionCallback(NeuralQueueActionCallback actionCallback) {
            this.actionCallback = actionCallback;
        }

        public void doAction() {
            if (actionCallback != null)
                actionCallback.actionCallback();
        }
    }

    private static class NeuralQueueFakeItem extends NeuralQueueItem {
        NeuralQueueFakeItem(File file) {
            super(file);
        }
    }

    private static class NeuralQueueStyleItem extends NeuralQueueItem {
        NeuralQueueStyleItem(File file) {
            super(file);
            this.type = QUEUED_STYLE;
            this.actionText = "outputTreeTableLoadButtonText";
//            this.status.setValue("Queued");
        }
    }

    private static class NeuralQueueImageItem extends NeuralQueueItem {
        NeuralQueueImageItem(File file) {
            super(file);
            this.type = QUEUED_IMAGE;
            this.actionText = "outputTreeTableInitButtonText";
            this.status.setValue(String.valueOf(FileUtils.parseImageIteration(file)));
        }
    }
}
