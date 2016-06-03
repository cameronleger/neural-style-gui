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
import java.util.logging.Level;
import java.util.logging.Logger;

import static javafx.application.Platform.exit;

public class Main extends Application {

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
            loader.setLocation(getClass().getResource("/main.fxml"));
            loader.setResources(bundle);
            root = loader.load();
            controller = loader.getController();
            controller.setStage(primaryStage);
        } catch (Exception e) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, e);
            exit();
            return;
        }

        try {
            primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    controller.stopService();
                }
            });
            primaryStage.setTitle(bundle.getString("title"));
            primaryStage.setScene(new Scene(root));
            primaryStage.setMinHeight(300);
            primaryStage.setMinWidth(500);
            primaryStage.show();
        } catch (Exception e) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, e);
        }
    }
}
