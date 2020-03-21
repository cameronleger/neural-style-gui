package com.cameronleger.neuralstylegui.model.properties;

import javafx.beans.property.*;

public class NeuralString extends NeuralProperty<String> {

    private StringProperty value;
    private final static String GLOBAL_DEFAULT = "";

    public NeuralString(String name) {
        super(name);
        this.defaultValue = GLOBAL_DEFAULT;
        this.value = new SimpleStringProperty(GLOBAL_DEFAULT);
    }

    public NeuralString(String name, String prettyName) {
        super(name, prettyName);
        this.defaultValue = GLOBAL_DEFAULT;
        this.value = new SimpleStringProperty(GLOBAL_DEFAULT);
    }

    public NeuralString(String name, String prettyName, String value) {
        super(name, prettyName);
        this.defaultValue = value;
        this.value = new SimpleStringProperty(value);
    }

    @Override
    public String getValue() {
        return this.value.getValue();
    }

    @Override
    public Property<String> valueProperty() {
        return this.value;
    }

    @Override
    public void setValue(String value) {
        this.value.setValue(value);
    }

    @Override
    public void reset() {
        this.value.setValue(this.defaultValue);
    }

}
