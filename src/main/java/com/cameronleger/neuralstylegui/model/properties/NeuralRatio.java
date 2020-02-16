package com.cameronleger.neuralstylegui.model.properties;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public interface NeuralRatio {

    DoubleProperty ratio = new SimpleDoubleProperty(1.0);

    public default Double getRatio() {
        return this.ratio.get();
    }

    public default DoubleProperty ratioProperty() {
        return this.ratio;
    }

    public default void setRatio(Number ratio) {
        this.ratio.setValue(ratio);
    }

    public void applyRatio();

}
