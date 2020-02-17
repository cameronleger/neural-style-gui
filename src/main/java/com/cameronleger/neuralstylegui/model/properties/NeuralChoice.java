package com.cameronleger.neuralstylegui.model.properties;

import javafx.beans.property.*;

public class NeuralChoice extends NeuralProperty<String> {

    private StringProperty value;
    private String[] choices;
    private final static String GLOBAL_DEFAULT = "";

    public NeuralChoice(String name) {
        super(name);
        this.defaultValue = GLOBAL_DEFAULT;
        this.value = new SimpleStringProperty(GLOBAL_DEFAULT);
    }

    public NeuralChoice(String name, String prettyName, String[] choices) {
        super(name, prettyName);
        this.choices = choices;
        this.defaultValue = choices[0];
        this.value = new SimpleStringProperty(choices[0]);
    }

    public NeuralChoice(String name, String prettyName, String value, String[] choices) {
        super(name, prettyName);
        this.choices = choices;
        this.defaultValue = value;
        this.value = new SimpleStringProperty(value);
    }

    public String[] getChoices() {
        return choices;
    }

    public void setChoices(String[] choices) {
        this.choices = choices;
    }

    @Override
    public String getValue() {
        return this.value.get();
    }

    @Override
    public Property<String> valueProperty() {
        return this.value;
    }

    @Override
    public void setValue(String value) {
        this.value.set(value);
    }

    @Override
    public void reset() {
        this.value.set(defaultValue);
    }

}
