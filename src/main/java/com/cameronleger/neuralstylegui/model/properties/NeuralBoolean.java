package com.cameronleger.neuralstylegui.model.properties;

import javafx.beans.property.*;

public class NeuralBoolean extends NeuralProperty<Boolean> {

    private BooleanProperty value;
    private final static boolean GLOBAL_DEFAULT = true;

    public NeuralBoolean(String name) {
        super(name);
        this.defaultValue = GLOBAL_DEFAULT;
        this.value = new SimpleBooleanProperty(GLOBAL_DEFAULT);
    }

    public NeuralBoolean(String name, String prettyName) {
        super(name, prettyName);
        this.defaultValue = GLOBAL_DEFAULT;
        this.value = new SimpleBooleanProperty(GLOBAL_DEFAULT);
    }

    public NeuralBoolean(String name, boolean value) {
        super(name);
        this.defaultValue = value;
        this.value = new SimpleBooleanProperty(value);
    }

    public NeuralBoolean(String name, String prettyName, boolean value) {
        super(name, prettyName);
        this.defaultValue = value;
        this.value = new SimpleBooleanProperty(value);
    }

    @Override
    public Boolean getValue() {
        return this.value.get();
    }

    @Override
    public Property<Boolean> valueProperty() {
        return this.value;
    }

    @Override
    public void setValue(Boolean value) {
        this.value.set(value);
    }

    @Override
    public void reset() {
        this.value.set(defaultValue);
    }

}
