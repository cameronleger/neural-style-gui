package com.cameronleger.neuralstylegui.model;

import com.cameronleger.neuralstyle.FileUtils;
import javafx.beans.property.*;

import java.io.File;

public class QueueItem {
    private StringProperty name;
    private ObjectProperty<File> file;
    private IntegerProperty iteration;

    public QueueItem(File file) {
        if (file == null) {
            this.name = new SimpleStringProperty("");
            this.file = new SimpleObjectProperty<>(null);
            this.iteration = new SimpleIntegerProperty(-1);
        } else {
            this.name = new SimpleStringProperty(file.getName());
            this.file = new SimpleObjectProperty<>(file);
            this.iteration = new SimpleIntegerProperty(FileUtils.parseImageIteration(file));
        }
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public ObjectProperty<File> fileProperty() {
        return file;
    }

    public File getFile() {
        return file.get();
    }

    public void setFile(File file) {
        this.file.set(file);
    }

    public IntegerProperty iterationProperty() {
        return iteration;
    }

    public int getIteration() {
        return iteration.get();
    }

    public void setIteration(int iteration) {
        this.iteration.set(iteration);
    }
}
