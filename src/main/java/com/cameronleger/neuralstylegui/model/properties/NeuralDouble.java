package com.cameronleger.neuralstylegui.model.properties;

import javafx.beans.property.*;

public class NeuralDouble extends NeuralProperty<Number> implements NeuralRatio {

    private DoubleProperty value;
    private final static double GLOBAL_DEFAULT = 1.0;

    public NeuralDouble(String name) {
        super(name);
        this.defaultValue = GLOBAL_DEFAULT;
        this.value = new SimpleDoubleProperty(GLOBAL_DEFAULT);
    }

    public NeuralDouble(String name, String prettyName) {
        super(name, prettyName);
        this.defaultValue = GLOBAL_DEFAULT;
        this.value = new SimpleDoubleProperty(GLOBAL_DEFAULT);
    }

    public NeuralDouble(String name, double value) {
        super(name);
        this.defaultValue = value;
        this.value = new SimpleDoubleProperty(value);
    }

    public NeuralDouble(String name, String prettyName, double value) {
        super(name, prettyName);
        this.defaultValue = value;
        this.value = new SimpleDoubleProperty(value);
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
