package com.cameronleger.neuralstylegui.component;

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

}
