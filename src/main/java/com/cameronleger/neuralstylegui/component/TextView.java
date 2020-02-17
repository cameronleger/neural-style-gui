package com.cameronleger.neuralstylegui.component;

import com.cameronleger.neuralstylegui.model.properties.NeuralString;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class TextView extends HBox {

    private static final Logger log = Logger.getLogger(TextView.class.getName());

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Label label;

    @FXML
    private TextField value;

    @FXML
    private Button resetButton;

    private NeuralString property;

    private String tooltip = "";

    public TextView() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/textView.fxml"));
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
        assert label != null : "fx:id=\"label\" was not injected: check your FXML file 'textView.fxml'.";
        assert value != null : "fx:id=\"value\" was not injected: check your FXML file 'textView.fxml'.";
        assert resetButton != null : "fx:id=\"resetButton\" was not injected: check your FXML file 'textView.fxml'.";
        label.setLabelFor(value);
        resetButton.setTooltip(new Tooltip("Reset to Default"));
        resetButton.setOnAction(e -> this.property.reset());
    }

    public void link(NeuralString property) {
        this.property = property;
        label.setText(property.getPrettyName());
        value.setText(property.getValue());
        value.textProperty().bindBidirectional(property.valueProperty());
    }

    public String getTooltip() {
        return this.tooltip;
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
        Tooltip tt = new Tooltip(tooltip);
        tt.setWrapText(true);
        tt.setMaxWidth(300);
        label.setTooltip(tt);
        value.setTooltip(tt);
    }

}
