package com.cameronleger.neuralstylegui.component;

import com.cameronleger.neuralstylegui.model.properties.NeuralChoice;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class ChoiceView extends HBox {

    private static final Logger log = Logger.getLogger(ChoiceView.class.getName());

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Label label;

    @FXML
    private ChoiceBox<String> value;

    @FXML
    private Button resetButton;

    private NeuralChoice property;

    private String tooltip = "";

    public ChoiceView() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/choiceView.fxml"));
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
        assert label != null : "fx:id=\"label\" was not injected: check your FXML file 'choiceView.fxml'.";
        assert value != null : "fx:id=\"value\" was not injected: check your FXML file 'choiceView.fxml'.";
        assert resetButton != null : "fx:id=\"resetButton\" was not injected: check your FXML file 'choiceView.fxml'.";
        label.setLabelFor(value);
        resetButton.setTooltip(new Tooltip("Reset to Default"));
        resetButton.setOnAction(e -> this.property.reset());
    }

    public void link(NeuralChoice property) {
        this.property = property;
        label.setText(property.getPrettyName());
        value.setItems(FXCollections.observableArrayList(property.getChoices()));
        value.setValue(property.getValue());
        value.valueProperty().bindBidirectional(property.valueProperty());
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
