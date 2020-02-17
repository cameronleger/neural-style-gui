package com.cameronleger.neuralstylegui.component;

import com.cameronleger.neuralstylegui.model.properties.NeuralDouble;
import com.cameronleger.neuralstylegui.model.properties.NeuralInt;
import com.cameronleger.neuralstylegui.model.properties.NeuralProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class NumberViewController {

    private static final Logger log = Logger.getLogger(NumberViewController.class.getName());

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Label label;

    @FXML
    private Slider slider;

    @FXML
    private TextField value;

    @FXML
    private TextField ratio;

    @FXML
    private Button resetButton;

    private NeuralProperty<Number> property;

    private String tooltip = "";

    public NumberViewController() {
        log.info("NumberViewController Created");
    }

    @FXML
    void initialize() {
        log.info("NumberViewController initialized");
        assert label != null : "fx:id=\"label\" was not injected: check your FXML file 'numberView.fxml'.";
        assert slider != null : "fx:id=\"slider\" was not injected: check your FXML file 'numberView.fxml'.";
        assert value != null : "fx:id=\"value\" was not injected: check your FXML file 'numberView.fxml'.";
        assert ratio != null : "fx:id=\"ratio\" was not injected: check your FXML file 'numberView.fxml'.";
        assert resetButton != null : "fx:id=\"resetButton\" was not injected: check your FXML file 'numberView.fxml'.";
        label.setLabelFor(value);
        ratio.setTooltip(new Tooltip("Ratio for Chaining"));
        resetButton.setTooltip(new Tooltip("Reset to Default"));
    }

    public void linkToInt(NeuralInt property) {
        this.property = property;
        label.setText(property.getPrettyName());
        value.setText(NeuralInt.INT_CONVERTER.toString(property.getValue()));
        slider.setValue(property.getValue().doubleValue());
        ratio.setText(NeuralDouble.DOUBLE_CONVERTER.toString(property.getRatio()));

        value.textProperty().bindBidirectional(slider.valueProperty(), NeuralInt.INT_CONVERTER);
        value.textProperty().bindBidirectional(property.valueProperty(), NeuralInt.INT_CONVERTER);
    }

    public void linkToDouble(NeuralDouble property) {
        this.property = property;
        label.setText(property.getPrettyName());
        value.setText(NeuralDouble.DOUBLE_CONVERTER.toString(property.getValue()));
        slider.setValue(property.getValue().doubleValue());
        ratio.setText(NeuralDouble.DOUBLE_CONVERTER.toString(property.getRatio()));

        value.textProperty().bindBidirectional(slider.valueProperty(), NeuralDouble.DOUBLE_CONVERTER);
        value.textProperty().bindBidirectional(property.valueProperty(), NeuralDouble.DOUBLE_CONVERTER);
    }

    public void onResetButton() {
        property.reset();
    }

    public double getMin() {
        return slider.getMin();
    }

    public void setMin(double min) {
        slider.setMin(min);
    }

    public double getMax() {
        return slider.getMax();
    }

    public void setMax(double max) {
        slider.setMax(max);
    }

    public double getStep() {
        return slider.getMajorTickUnit();
    }

    public void setStep(double step) {
        slider.setMajorTickUnit(step);
    }

    public String getTooltip() {
        return this.tooltip;
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
        Tooltip tt = new Tooltip(tooltip);
        tt.setWrapText(true);
        label.setTooltip(tt);
        value.setTooltip(tt);
        slider.setTooltip(tt);
    }

}
