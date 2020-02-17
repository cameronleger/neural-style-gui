package com.cameronleger.neuralstylegui.model.properties;

import javafx.beans.property.*;

public class NeuralInt extends NeuralProperty<Number> implements NeuralRatio {

    private IntegerProperty value;
    private final static int GLOBAL_DEFAULT = 1;

    public NeuralInt(String name) {
        super(name);
        this.defaultValue = GLOBAL_DEFAULT;
        this.value = new SimpleIntegerProperty(GLOBAL_DEFAULT);
    }

    public NeuralInt(String name, String prettyName) {
        super(name, prettyName);
        this.defaultValue = GLOBAL_DEFAULT;
        this.value = new SimpleIntegerProperty(GLOBAL_DEFAULT);
    }

    public NeuralInt(String name, int value) {
        super(name);
        this.defaultValue = value;
        this.value = new SimpleIntegerProperty(value);
    }

    public NeuralInt(String name, String prettyName, int value) {
        super(name, prettyName);
        this.defaultValue = value;
        this.value = new SimpleIntegerProperty(value);
    }

    @Override
    public Number getValue() {
        return this.value.getValue();
    }

    @Override
    public Property<Number> valueProperty() {
        return this.value;
    }

    @Override
    public void setValue(Number value) {
        this.value.setValue(value);
    }

    @Override
    public void reset() {
        this.value.setValue(this.defaultValue);
    }

    @Override
    public void applyRatio() {
        this.value.multiply(this.ratio.getValue());
    }

}
