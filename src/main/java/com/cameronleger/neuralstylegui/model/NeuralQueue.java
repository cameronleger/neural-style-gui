package com.cameronleger.neuralstylegui.model;

import com.cameronleger.neuralstyle.FileUtils;
import com.cameronleger.neuralstyle.NeuralStyle;
import javafx.beans.property.*;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.ResourceBundle;

public final class NeuralQueue {
    public final static int QUEUED_STYLE = 1;
    public final static int QUEUED_IMAGE = 2;

    private static ResourceBundle bundle;

    public static void setBundle(ResourceBundle bundle) {
        NeuralQueue.bundle = bundle;
    }

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

        public void setFile(File file) {
            this.file.set(file);
        }

        public StringProperty getName() {
            return name;
        }

        public StringProperty getStatus() {
            return status;
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

        public void changeStatus(int newStatus) {
            return;
        }
    }

    private static class NeuralQueueFakeItem extends NeuralQueueItem {
        NeuralQueueFakeItem(File file) {
            super(file);
        }
    }

    private static class NeuralQueueStyleItem extends NeuralQueueItem {
        private NeuralStyle style;

        NeuralQueueStyleItem(File file) {
            super(file);
            this.type = QUEUED_STYLE;
            this.actionText = bundle.getString("outputTreeTableLoadButtonText");
            updateStyle();
        }

        @Override
        public void setFile(File file) {
            super.setFile(file);
            updateStyle();
        }

        private void updateStyle() {
            style = FileUtils.loadStyle(file.getValue());
            if (style == null)
                this.status.setValue(bundle.getString("neuralQueueItemInvalidFile"));
            else {
                String statusValue = "";
                switch (style.getQueueStatus()) {
                    case NeuralStyle.INVALID_FILE:
                        statusValue = bundle.getString("neuralQueueItemInvalidFile");
                        break;
                    case NeuralStyle.INVALID_ARGUMENTS:
                        statusValue = bundle.getString("neuralQueueItemInvalidArguments");
                        break;
                    case NeuralStyle.QUEUED:
                        statusValue = bundle.getString("neuralQueueItemQueued");
                        break;
                    case NeuralStyle.IN_PROGRESS:
                        statusValue = bundle.getString("neuralQueueItemInProgress");
                        break;
                    case NeuralStyle.CANCELLED:
                        statusValue = bundle.getString("neuralQueueItemCancelled");
                        break;
                    case NeuralStyle.FAILED:
                        statusValue = bundle.getString("neuralQueueItemFailed");
                        break;
                    case NeuralStyle.FINISHED:
                        statusValue = bundle.getString("neuralQueueItemFinished");
                        break;
                }
                this.status.setValue(statusValue);
            }
        }

        @Override
        public void changeStatus(int newStatus) {
            if (style != null) {
                style.setQueueStatus(newStatus);
                FileUtils.saveOutputStyle(style);
            }
        }
    }

    private static class NeuralQueueImageItem extends NeuralQueueItem {
        NeuralQueueImageItem(File file) {
            super(file);
            this.type = QUEUED_IMAGE;
            this.actionText = bundle.getString("outputTreeTableInitButtonText");
            this.status.setValue(String.valueOf(FileUtils.parseImageIteration(file)));
        }
    }
}
