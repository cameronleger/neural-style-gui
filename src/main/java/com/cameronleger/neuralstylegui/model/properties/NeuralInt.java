package com.cameronleger.neuralstylegui.model.properties;

import javafx.beans.property.*;
import javafx.util.StringConverter;

public class NeuralInt extends NeuralProperty<Number> {

    private IntegerProperty value;
    private final static int GLOBAL_DEFAULT = 1;
    private DoubleProperty ratio = new SimpleDoubleProperty(1.0);

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

    public Double getRatio() {
        return this.ratio.get();
    }

    public DoubleProperty ratioProperty() {
        return this.ratio;
    }

    public void setRatio(Number ratio) {
        this.ratio.setValue(ratio);
    }

    public static StringConverter<Number> INT_CONVERTER = new StringConverter<Number>() {
        @Override
        public String toString(Number t) {
            return String.valueOf(t.intValue());
        }

        @Override
        public Number fromString(String string) {
            try {
                return Integer.parseInt(string);
            } catch (Exception e) {
                return 0;
            }
        }
    };

}
