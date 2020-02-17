package com.cameronleger.neuralstylegui.component;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;

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
    private Slider slider;

    @FXML
    private TextField value;

    @FXML
    private TextField ratio;

    @FXML
    private Button resetButton;

    public NumberViewController() {
        log.info("NumberViewController Created");
    }

    @FXML
    void initialize() {
        log.info("NumberViewController initialized 1");
        assert slider != null : "fx:id=\"slider\" was not injected: check your FXML file 'numberView.fxml'.";
        assert value != null : "fx:id=\"value\" was not injected: check your FXML file 'numberView.fxml'.";
        assert ratio != null : "fx:id=\"ratio\" was not injected: check your FXML file 'numberView.fxml'.";
        assert resetButton != null : "fx:id=\"resetButton\" was not injected: check your FXML file 'numberView.fxml'.";
    }

}
