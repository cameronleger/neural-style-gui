package com.cameronleger.neuralstylegui;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.*;

import static javafx.application.Platform.exit;

public class Main extends Application {
    private static final Logger log = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        ResourceBundle bundle = ResourceBundle.getBundle("main", Locale.US);
        FXMLLoader loader = new FXMLLoader();
        Parent root;
        MainController controller;

        try {
            log.log(Level.FINER, "Getting FXML loader and resources.");
            loader.setLocation(getClass().getResource("/main.fxml"));
            loader.setResources(bundle);
            root = loader.load();
            log.log(Level.FINER, "Getting controller.");
            controller = loader.getController();
            controller.setStage(primaryStage);
        } catch (Exception e) {
            log.log(Level.SEVERE, e.toString(), e);
            exit();
            return;
        }

        try {
            log.log(Level.FINER, "Setting main window parameters.");
            primaryStage.setOnCloseRequest(event -> controller.stopService());
            primaryStage.setTitle(bundle.getString("title"));
            primaryStage.setScene(new Scene(root));
            primaryStage.setMinHeight(300);
            primaryStage.setMinWidth(500);
            log.log(Level.FINER, "Showing main window.");
            primaryStage.show();
        } catch (Exception e) {
            log.log(Level.SEVERE, e.toString(), e);
        }
    }
}
