package com.cameronleger.neuralstylegui.model.properties;

import javafx.beans.property.*;

public abstract class NeuralProperty<T> {

    protected StringProperty name;
    protected StringProperty prettyName;
    protected T defaultValue;

    protected NeuralProperty(String name) {
        this.name = new SimpleStringProperty(name);
        this.prettyName = new SimpleStringProperty(name);
    }

    protected NeuralProperty(String name, String prettyName) {
        this.name = new SimpleStringProperty(name);
        this.prettyName = new SimpleStringProperty(prettyName);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getPrettyName() {
        return prettyName.get();
    }

    public StringProperty prettyNameProperty() {
        return prettyName;
    }

    public void setPrettyName(String prettyName) {
        this.prettyName.set(prettyName);
    }

    public abstract T getValue();

    public abstract Property<T> valueProperty();

    public abstract void setValue(T value);

    public abstract void reset();

}
