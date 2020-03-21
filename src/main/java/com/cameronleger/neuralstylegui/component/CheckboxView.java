package com.cameronleger.neuralstylegui.component;

import com.cameronleger.neuralstylegui.model.properties.NeuralBoolean;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class CheckboxView extends HBox {

    private static final Logger log = Logger.getLogger(CheckboxView.class.getName());

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private CheckBox value;

    @FXML
    private Button resetButton;

    private NeuralBoolean property;

    private String tooltip = "";

    public CheckboxView() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/checkboxView.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @FXML
    void initialize() {
        assert value != null : "fx:id=\"value\" was not injected: check your FXML file 'choiceView.fxml'.";
        assert resetButton != null : "fx:id=\"resetButton\" was not injected: check your FXML file 'choiceView.fxml'.";
        resetButton.setTooltip(new Tooltip("Reset to Default"));
        resetButton.setOnAction(e -> this.property.reset());
    }

    public void link(NeuralBoolean property) {
        this.property = property;
        value.setText(property.getPrettyName());
        value.setSelected(property.getValue());
        value.selectedProperty().bindBidirectional(property.valueProperty());
    }

    public String getTooltip() {
        return this.tooltip;
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
        Tooltip tt = new Tooltip(tooltip);
        tt.setWrapText(true);
        tt.setMaxWidth(300);
        value.setTooltip(tt);
    }

}
