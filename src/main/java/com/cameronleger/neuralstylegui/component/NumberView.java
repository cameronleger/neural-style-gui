package com.cameronleger.neuralstylegui.component;

import com.cameronleger.neuralstylegui.model.properties.NeuralDouble;
import com.cameronleger.neuralstylegui.model.properties.NeuralInt;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.util.logging.Logger;

public class NumberView extends HBox {

    private static final Logger log = Logger.getLogger(NumberView.class.getName());

    private NumberViewController controller;

    public NumberView() {
        log.info("NumberView Created");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/numberView.fxml"));
        fxmlLoader.setRoot(this);

        fxmlLoader.setControllerFactory(param -> controller = new NumberViewController());

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public void linkToInt(NeuralInt property) {
        controller.linkToInt(property);
    }

    public void linkToDouble(NeuralDouble property) {
        controller.linkToDouble(property);
    }

    public double getMin() {
        return controller.getMin();
    }

    public void setMin(double min) {
        controller.setMin(min);
    }

    public double getMax() {
        return controller.getMax();
    }

    public void setMax(double max) {
        controller.setMax(max);
    }

    public double getStep() {
        return controller.getStep();
    }

    public void setStep(double step) {
        controller.setStep(step);
    }

    public String getTooltip() {
        return controller.getTooltip();
    }

    public void setTooltip(String tooltip) {
        controller.setTooltip(tooltip);
    }

}
