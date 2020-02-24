package com.cameronleger.neuralstylegui.model;

import com.cameronleger.neuralstyle.FileUtils;
import com.cameronleger.neuralstyle.NeuralStyleV3;
import javafx.beans.property.*;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.ResourceBundle;

public final class NeuralQueue {
    public final static int QUEUED_PARENT = 0;
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
        int statusCode = -100;
        String actionText = "";
        NeuralQueueActionCallback actionCallback;
        String baseName = "";
        long chainIteration = 0;
        long imageIteration = 0;

        NeuralQueueItem(File file) {
            if (file != null) {
                this.file.setValue(file);
                FileUtils.NeuralFilePortions portions = new FileUtils.NeuralFilePortions(file);
                this.baseName = portions.baseName;
                this.chainIteration = portions.chainIteration;
                this.imageIteration = portions.imageIteration;
                this.name.setValue(FileUtils.getFileName(file));
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

        public int getStatusCode() {
            return statusCode;
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

        public String getBaseName() {
            return baseName;
        }

        public long getChainIteration() {
            return chainIteration;
        }

        public long getImageIteration() {
            return imageIteration;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof NeuralQueueItem) {
                return ((NeuralQueueItem) obj).getName().get().equals(this.getName().get());
            } else return false;
        }

    }

    private static class NeuralQueueFakeItem extends NeuralQueueItem {
        NeuralQueueFakeItem(File file) {
            super(file);
        }
    }

    private static class NeuralQueueStyleItem extends NeuralQueueItem {
        private NeuralStyleV3 style;

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

        private void updateStatusText() {
            if (style == null)
                this.status.setValue(bundle.getString("neuralQueueItemInvalidFile"));
            else {
                String statusValue = "";
                this.statusCode = style.getQueueStatus();
                switch (style.getQueueStatus()) {
                    case NeuralStyleV3.INVALID_FILE:
                        statusValue = bundle.getString("neuralQueueItemInvalidFile");
                        break;
                    case NeuralStyleV3.INVALID_ARGUMENTS:
                        statusValue = bundle.getString("neuralQueueItemInvalidArguments");
                        break;
                    case NeuralStyleV3.QUEUED:
                        statusValue = bundle.getString("neuralQueueItemQueued");
                        break;
                    case NeuralStyleV3.IN_PROGRESS:
                        statusValue = bundle.getString("neuralQueueItemInProgress");
                        break;
                    case NeuralStyleV3.CANCELLED:
                        statusValue = bundle.getString("neuralQueueItemCancelled");
                        break;
                    case NeuralStyleV3.FAILED:
                        statusValue = bundle.getString("neuralQueueItemFailed");
                        break;
                    case NeuralStyleV3.FINISHED:
                        statusValue = bundle.getString("neuralQueueItemFinished");
                        break;
                    case NeuralStyleV3.PARENT:
                        this.type = QUEUED_PARENT;
                        statusValue = bundle.getString("neuralQueueItemParent");
                        break;
                }
                this.status.setValue(statusValue);
            }
        }

        private void updateStyle() {
            style = FileUtils.loadStyle(file.getValue());
            updateStatusText();
        }

        @Override
        public void changeStatus(int newStatus) {
            if (style != null) {
                style.setQueueStatus(newStatus);
                updateStatusText();
                if (this.type == QUEUED_STYLE)
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
