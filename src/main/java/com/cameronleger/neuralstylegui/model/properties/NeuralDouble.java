package com.cameronleger.neuralstylegui.model.properties;

import javafx.beans.property.*;
import javafx.util.StringConverter;

import java.text.DecimalFormat;

public class NeuralDouble extends NeuralProperty<Number> {

    private DoubleProperty value;
    private final static double GLOBAL_DEFAULT = 1.0;
    private DoubleProperty ratio = new SimpleDoubleProperty(1.0);

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

    public Double getRatio() {
        return this.ratio.get();
    }

    public DoubleProperty ratioProperty() {
        return this.ratio;
    }

    public void setRatio(Number ratio) {
        this.ratio.setValue(ratio);
    }

    public static StringConverter<Number> DOUBLE_CONVERTER = new StringConverter<Number>() {
        DecimalFormat format = new DecimalFormat("#.#####");

        @Override
        public String toString(Number t) {
            return format.format(t);
        }

        @Override
        public Number fromString(String string) {
            try {
                return Double.parseDouble(string);
            } catch (Exception e) {
                return 0;
            }
        }
    };

}
