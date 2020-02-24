package com.cameronleger.neuralstylegui.model;

import com.cameronleger.neuralstyle.FileUtils;

import java.io.File;

public class NeuralQueueFile {

    public enum ChangeType {
        NEW,
        MODIFIED,
        DELETED,
        UNKNOWN
    }

    private String filePath;
    private String fileName;
    private ChangeType changeType;
    private NeuralQueue.NeuralQueueItem queueItem = null;

    public NeuralQueueFile(File file, ChangeType changeType) {
        this.filePath = file.getAbsolutePath();
        this.fileName = FileUtils.getFileName(file);
        if (!FileUtils.isFileBeingWritten(file))
            this.queueItem = NeuralQueue.createQueueItem(file);
        this.changeType = changeType;
    }

    public NeuralQueueFile(String filePath, ChangeType changeType) {
        File file = new File(filePath);
        this.filePath = file.getAbsolutePath();
        this.fileName = FileUtils.getFileName(file);
        if (!FileUtils.isFileBeingWritten(file))
            this.queueItem = NeuralQueue.createQueueItem(file);
        this.changeType = changeType;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public ChangeType getChangeType() {
        return changeType;
    }

    public NeuralQueue.NeuralQueueItem getQueueItem() {
        if (queueItem == null)
            this.queueItem = NeuralQueue.createQueueItem(new File(filePath));
        return queueItem;
    }
}
